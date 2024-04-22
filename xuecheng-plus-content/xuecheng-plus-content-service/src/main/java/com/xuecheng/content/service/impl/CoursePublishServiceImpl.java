package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.feignclient.model.CourseIndex;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.content.jobhandler.CoursePublishTask;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @description 课程发布服务实现类
* @author TMC
* @date 2023/2/16 16:19
* @version 1.0
*/
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseMarketServiceImpl courseMarketService;

    @Resource
    MqMessageService mqMessageService;

    @Resource
    CoursePublishPreMapper coursePublishPreMapper;

    @Resource
    CoursePublishMapper coursePublishMapper;

    @Resource
    CourseInfoService courseBaseInfoService;

    @Resource
    TeachplanService teachplanService;

    @Resource
    CourseTeacherMapper courseTeacherMapper;

    @Resource
    MediaServiceClient mediaServiceClient;

    @Resource
    SearchServiceClient searchServiceClient;

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public CoursePreviewDto previewCourse(Long courseId) {

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfoService.getCourseInfoById(courseId));
        coursePreviewDto.setTeachplans(teachplanService.getTeachplanById(courseId));
        coursePreviewDto.setCourseTeacher(courseTeacherMapper.selectOne(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId)));
        return coursePreviewDto;
    }

    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {

        //1.待提交课程约束校验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        List<TeachplanDto> teachplanDtos = teachplanService.getTeachplanById(courseId);
        CourseTeacher courseTeacher = courseTeacherMapper.selectOne(new LambdaQueryWrapper<CourseTeacher>()
                        .eq(CourseTeacher::getCourseId, courseId));
        //1.1.待提交课程是否存在
        if (courseBase == null) {
            XcException.cast("待提交课程不存在");
        }
        //1.2.待提交课程是否提供图片
        if (StringUtils.isBlank(courseBase.getPic())) {
            XcException.cast("提交失败,请上传课程图片");
        }
        //1.3.待提交课程是否提供课程计划
        if (teachplanDtos == null || teachplanDtos.size() == 0) {
            XcException.cast("提交失败,请添加课程计划");
        }
        //1.4.待提交课程是否提供教师信息
        if (courseTeacher == null) {
            XcException.cast("提交失败,请添加课程教师信息");
        }
        //1.5.待提交课程是否重复提交
        if (Dictionary.CourseBase.AuditStatus_SUBMITTED.getCode().equals(courseBase.getAuditStatus())) {
            XcException.cast("当前为等待审核状态,审核完成方可再次提交");
        }
        //1.6.待提交课程是否属于本机构
        if (!companyId.equals(courseBase.getCompanyId())) {
            XcException.cast("不允许提交其它机构的课程");
        }

        //2.保存至course_publish_pre表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //2.1.封装课程基本信息
        CourseInfoDto courseBaseInfo = courseBaseInfoService.getCourseInfoById(courseId);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        //2.2.封装课程营销信息
        String courseMarketJson = JSON.toJSONString(courseMarketService.getById(courseId));
        coursePublishPre.setMarket(courseMarketJson);
        //2.3.封装课程计划信息
        String teachplanJson = JSON.toJSONString(teachplanDtos);
        coursePublishPre.setTeachplan(teachplanJson);
        //2.4.封装课程教师信息
        String courseTeacherJson = JSON.toJSONString(courseTeacher);
        coursePublishPre.setTeachers(courseTeacherJson);
        //2.5.设置机构信息
        coursePublishPre.setCompanyId(companyId);
        //2.6.设置课程审核状态为已提交
        coursePublishPre.setStatus(Dictionary.CourseBase.AuditStatus_SUBMITTED.getCode());
        //2.7.提交
        CoursePublishPre coursePublishPreSave = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreSave == null) {
            int insertCoursePublishPre = coursePublishPreMapper.insert(coursePublishPre);
            if (insertCoursePublishPre < 1) {
                log.error("添加课程预发布记录出错");
                XcException.cast("课程提交审核失败");
            }
        }else {
            int updateCoursePublishPre = coursePublishPreMapper.updateById(coursePublishPre);
            if (updateCoursePublishPre < 1) {
                log.error("更新课程预发布记录出错");
                XcException.cast("课程提交审核失败");
            }
        }

        //3.更新course_base表信息
        courseBase.setAuditStatus(Dictionary.CourseBase.AuditStatus_SUBMITTED.getCode());
        int updateCourseBase = courseBaseMapper.updateById(courseBase);
        if (updateCourseBase < 1) {
            log.error("课程提交审核过程更新课程审核状态出错");
            XcException.cast("课程提交审核失败");
        }
    }

    @Transactional
    @Override
    public void publishCourse(Long companyId, Long courseId) {

        //1.约束校验
        //1.1.课程是否已提交审核
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XcException.cast("请先提交课程审核,审核通过才可以发布");
        }
        //1.2.课程是否已审核通过
        if (!Dictionary.CourseBase.AuditStatus_APPROVED.getCode().equals(coursePublishPre.getStatus())) {
            XcException.cast("操作失败,课程审核通过方可发布");
        }
        //1.3.课程是否为所属机构课程
        if (!companyId.equals(coursePublishPre.getCompanyId())) {
            XcException.cast("不允许发布其它机构的课程");
        }

        //2.保存课程发布信息
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus(Dictionary.CoursePublish.Status_PUBLISHED.getCode());
        CoursePublish coursePublishSave = coursePublishMapper.selectById(courseId);
        if (coursePublishSave == null) {
            int insertCoursePublish = coursePublishMapper.insert(coursePublish);
            if (insertCoursePublish < 1) {
                log.error("课程发布表添加记录出错");
                XcException.cast("课程发布失败");
            }
        }else {
            int updateCoursePublish = coursePublishMapper.updateById(coursePublish);
            if (updateCoursePublish < 1) {
                log.error("课程发布表更新记录出错");
                XcException.cast("课程发布失败");
            }
        }
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XcException.cast("待发布课程的基本信息不存在");
        }
        courseBase.setStatus(Dictionary.CourseBase.Status_PUBLISHED.getCode());
        int updateCourseBase = courseBaseMapper.updateById(courseBase);
        if (updateCourseBase < 1) {
            XcException.cast("更新待发布课程基本信息失败");
        }

        //3.保存消息表
        this.saveCoursePublishMessage(courseId);

        //4.删除course_publish_pre对应记录
        int deleteCoursePublishPre = coursePublishPreMapper.deleteById(courseId);
        if (deleteCoursePublishPre < 1) {
            log.error("删除课程预发布表记录失败");
            XcException.cast("课程发布失败");
        }

    }

    @Override
    public File generateCourseHtml(Long courseId) {

        //0.声明课程静态化文件
        File course = null;

        try {
            //1.配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());
            //1.1.设置模板文件路径,classpath/templates/
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //1.2.设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //2.加载模板文件
            Template template = configuration.getTemplate("course_template.ftl");

            //3.获取数据模型
            CoursePreviewDto coursePreviewDto = this.previewCourse(courseId);
            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewDto);

            //4.课程静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            //4.1.创建课程静态化页面临时文件
             course = File.createTempFile("course", ".html");
            //4.2.将静态化内容输出到临时文件并返回
            try (InputStream inputStream = IOUtils.toInputStream(content);
                 FileOutputStream outputStream = new FileOutputStream(course)) {
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (Exception e) {
            log.error("课程静态化异常:{}", e.getMessage());
        }

        return course;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {

        try {
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            mediaServiceClient.upload(multipartFile, "course", courseId + ".html");
        } catch (Exception e) {
            log.error("上传课程静态化页面文件异常:{}", e.getMessage());
        }

    }

    @Override
    public Boolean saveCourseIndex(Long courseId) {

        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        if (coursePublish == null) {
            log.error("待保存索引的课程信息不存在");
            return false;
        }
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        Boolean add = false;
        try {
           add = searchServiceClient.addIndex(courseIndex);
        }catch (Exception e) {
            log.error("保存课程索引过程出错,课程id:{},错误信息:{}", courseId, e.getMessage());
        }
        return add;
    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {

        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);

        if (coursePublish != null && Dictionary.CoursePublish.Status_PUBLISHED.getCode().equals(coursePublish.getStatus())) {

            return coursePublish;

        }

        return null;
    }


    /**
    * @description 添加课程发布消息记录
    * @param courseId 课程id
    * @return void
    * @author TMC
    * @date 2023/2/17 21:48
    */
    private void saveCoursePublishMessage(Long courseId) {

        MqMessage mqMessage = mqMessageService.addMessage(CoursePublishTask.MESSAGE_TYPE, String.valueOf(courseId),
                null, null);
        if (mqMessage == null) {
            log.error("添加课程发布消息记录失败");
            XcException.cast("课程发布失败");
        }
    }


}
