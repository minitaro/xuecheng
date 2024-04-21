package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
* @description 课程信息服务实现类
* @author TMC
* @date 2023/2/6 10:38
* @version 1.0
*/
@Slf4j
@Service
public class CourseInfoServiceImpl implements CourseInfoService {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseMarketMapper courseMarketMapper;

    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Resource
    CourseMarketServiceImpl courseMarketService;

    @Resource
    TeachplanMapper teachplanMapper;

    @Resource
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams,
                                                      QueryCourseParamsDto queryCourseParamsDto) {

        //1.构建查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName,
                queryCourseParamsDto.getCourseName()); //1.1.根据课程名称查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus()); //1.2.根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus,
                queryCourseParamsDto.getPublishStatus()); //1.3.根据课程发布状态查询
        queryWrapper.eq(CourseBase::getCompanyId, companyId); //1.4.根据机构查询

        //2.构建分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //3.分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //4.查询结果封装
        List<CourseBase> list = pageResult.getRecords(); //4.1.获取记录列表
        long total = pageResult.getTotal(); //4.2.获取记录总数
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(list, total, pageParams.getPageNo(),
                pageParams.getPageSize()); //4.3.封装结果集

        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseInfoDto addCourse(Long companyId, AddCourseDto addCourseDto) {

        //1.添加课程基本信息
        CourseBase courseBase = new CourseBase(); //1.1.创建po
        BeanUtils.copyProperties(addCourseDto, courseBase); //1.2.将课程信息复制给新增po
        courseBase.setCompanyId(companyId); //1.3.设置所属机构id

        courseBase.setAuditStatus(Dictionary.CourseBase.AuditStatus_TO_BE_SUBMITTED.getCode()); //1.4.设置审核状态:默认未提交
        courseBase.setStatus(Dictionary.CourseBase.Status_UNPUBLISHED.getCode()); //1.5.设置发布状态:默认未发布
        int insertCourseBase = courseBaseMapper.insert(courseBase); //1.6.插入新增po
        if (insertCourseBase < 1 ) {
            log.error("新增课程出错:{}", courseBase);
            XcException.cast("新增课程失败");
        }

        //2.添加课程营销信息
        CourseMarket courseMarket = new CourseMarket(); //2.1.新增po
        BeanUtils.copyProperties(addCourseDto, courseMarket); //2.2.将课程信息复制给新增po
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId); //2.3.设置课程id
        int insertCourseMarket = this.saveCourseMarket(courseMarket); //2.4.插入新增po
        if (insertCourseMarket < 1) {
            log.error("添加课程营销信息出错:{}", courseMarket);
            XcException.cast("新增课程失败");
        }

        //3.查询并返回新增课程信息
        return this.getCourseInfoById(courseId);
    }

    @Override
    public CourseInfoDto getCourseInfoById(Long courseId) {

        //1.查询指定id课程是否存在
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XcException.cast("该课程信息不存在");
        }

        //2.创建响应dto,封装课程基本信息
        CourseInfoDto courseInfoDto = new CourseInfoDto();
        BeanUtils.copyProperties(courseBase, courseInfoDto);

        //3.查询和封装课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseInfoDto);
        }

        //4.查询和封装课程大分类信息
        CourseCategory mtCategory = courseCategoryMapper.selectById(courseBase.getMt());
        if (mtCategory != null) {
            courseInfoDto.setMtName(mtCategory.getName());
        }

        //5.查询和封装课程小分类信息
        CourseCategory stCategory = courseCategoryMapper.selectById(courseBase.getSt());
        if (stCategory != null) {
            courseInfoDto.setStName(stCategory.getName());
        }

        return courseInfoDto;
    }

    @Transactional
    @Override
    public CourseInfoDto editCourse(Long companyId, EditCourseDto editCourseDto) {

        //1.校验
        //1.1.校验指定id课程是否存在
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            log.error("待修改课程的查询结果为空,课程id:{}", courseId);
            XcException.cast("该课程信息不存在");
        }
        //1.2.校验修改权限
        if (!companyId.equals(courseBase.getCompanyId())) {
            XcException.cast("只允许修改所属机构的课程");
        }

        //2.修改课程基本信息
        BeanUtils.copyProperties(editCourseDto, courseBase); //2.1.修改po
        int updateCourseBase = courseBaseMapper.updateById(courseBase); //2.2.提交po
        if (updateCourseBase < 1) {
            log.error("修改课程基本信息过程中出错:{}", courseBase);
            XcException.cast("修改课程失败");
        }

        //3.修改课程营销信息
        CourseMarket courseMarket = new CourseMarket(); //3.1.创建po
        BeanUtils.copyProperties(editCourseDto, courseMarket); //3.2.修改po
        int updateCourseMarket = this.saveCourseMarket(courseMarket); //3.3.提交po
        if (updateCourseMarket < 1) {
            log.error("修改课程营销信息过程中出错:{}", courseMarket);
            XcException.cast("修改课程失败");
        }

        return this.getCourseInfoById(courseId);
    }

    @Transactional
    @Override
    public void deleteCourseById(Long companyId, Long courseId) {
        //1.信息校验
        //1.1.校验指定id课程是否存在
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            log.error("待删除课程的查询结果为空,课程id:{}", courseId);
            XcException.cast("该课程信息不存在");
        }
        //1.2.校验权限
        if (!companyId.equals(courseBase.getCompanyId())) {
            XcException.cast("只允许修改所属机构的课程");
        }
        //1.3.校验状态
        if (!Dictionary.MediaFiles.AuditStatus_TO_BE_AUDITTED.getCode().equals(courseBase.getAuditStatus())) {
            XcException.cast("只允许删除未提交的课程");
        }

        //2.删除课程基本信息
        int deleteCourseBase = courseBaseMapper.deleteById(courseId);
        if (deleteCourseBase < 1) {
            log.error("删除课程基本信息出错:{}", courseBase);
            XcException.cast("删除课程失败");
        }

        //3.删除课程营销信息
        courseMarketMapper.deleteById(courseId);

        //4.删除课程计划
        LambdaQueryWrapper<Teachplan> teachplanQueryWrapper = new LambdaQueryWrapper<>();
        teachplanQueryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanQueryWrapper.eq(Teachplan::getStatus, 1);
        List<Teachplan> teachplans = teachplanMapper.selectList(teachplanQueryWrapper);
        teachplans.stream().forEach(teachplan -> {
            teachplan.setStatus(0);
            int update = teachplanMapper.updateById(teachplan);
            if (update < 1) {
                log.error("删除课程计划出错:{}", teachplan);
                XcException.cast("删除课程失败");
            }
        });

        //5.删除课程教师
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(queryWrapper);

    }

    /**
    * @description 添加或修改课程营销信息
    * @param courseMarket 课程营销信息
    * @return int
    * @author TMC
    * @date 2023/2/7 20:50
    */
    private int saveCourseMarket(CourseMarket courseMarket) {

        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b ? 1 : 0;

    }

}
