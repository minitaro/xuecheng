package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.CourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.CourseTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 用户课程表服务实现类
 * @author TMC
 * @date 2023/2/24 22:03
 * @version 1.0
 */
@Slf4j
@Service
public class CourseTableServiceImpl implements CourseTableService {

    @Resource
    XcChooseCourseMapper chooseCourseMapper;

    @Resource
    XcCourseTablesMapper courseTablesMapper;

    @Resource
    ContentServiceClient contentServiceClient;

    @Resource
    CourseTableService currentProxy;


    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {

        //1.远程查询课程发布信息
        CoursePublish coursePublish = contentServiceClient.getCoursePublish(courseId);
        if(coursePublish == null){
            XcException.cast("课程发布信息不存在");
        }

        //2.确定课程收费类型
        String charge = coursePublish.getCharge();
        String paid = Dictionary.CoursePublish.Charge_PAID.getCode();
        XcChooseCourse xcChooseCourse = null;
        if(paid.equals(charge)){
            //添加收费课程
            xcChooseCourse = currentProxy.addPaidCourse(userId, coursePublish);
        }else{
            //添加免费课程
            xcChooseCourse = currentProxy.addFreeCourse(userId, coursePublish);
        }

        //3.查询选课学习资格
        String access = this.queryCourseAccess(userId, courseId).getLearnStatus();
        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        xcChooseCourseDto.setLearnStatus(access);
        BeanUtils.copyProperties(xcChooseCourse, xcChooseCourseDto);
        return xcChooseCourseDto;

    }

    @Override
    public XcCourseTablesDto queryCourseAccess(String userId, Long courseId) {

        LambdaQueryWrapper<XcCourseTables> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XcCourseTables::getUserId, userId);
        queryWrapper.eq(XcCourseTables::getCourseId, courseId);
        XcCourseTables courseTables = courseTablesMapper.selectOne(queryWrapper);

        XcCourseTablesDto courseTablesDto = new XcCourseTablesDto();
        //1.课程不存在
        if(courseTables == null){
            //选课未成功:没有选课或选课后没有支付
            String unqualified = Dictionary.ChooseCourse.LearnStatus_UNQUALIFIED.getCode();
            courseTablesDto.setLearnStatus(unqualified);
            return courseTablesDto;
        }

        //2.课程是否过期
        boolean isExpired = courseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if(!isExpired){
            //正常学习
            String qualified = Dictionary.ChooseCourse.LearnStatus_QUALIFIED.getCode();
            courseTablesDto.setLearnStatus(qualified);
        }else{
            //已过期
            String expired = Dictionary.ChooseCourse.LearnStatus_EXPIRED.getCode();
            courseTablesDto.setLearnStatus(expired);
        }
        BeanUtils.copyProperties(courseTables, courseTablesDto);
        return courseTablesDto;
    }


    @Transactional
    @Override
    public XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish) {
        //1.查询待添加的免费选课记录是否存在
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        Long coursePublishId = coursePublish.getId();//课程id
        String orderType = Dictionary.ChooseCourse.OrderType_FREE.getCode();//免费课程
        String status = Dictionary.ChooseCourse.Status_SUCCESS.getCode();//选课成功
        queryWrapper = queryWrapper.eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursePublishId)
                .eq(XcChooseCourse::getOrderType, orderType)
                .eq(XcChooseCourse::getStatus, status);
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && xcChooseCourses.size() > 0) {
            //选课记录已添加,直接返回
            return xcChooseCourses.get(0);
        }

        //2.添加免费选课记录信息
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(coursePublishId);//课程id
        chooseCourse.setCourseName(coursePublish.getName());//课程名称
        chooseCourse.setCoursePrice(0F);//免费课程价格为0
        chooseCourse.setUserId(userId);//用户id
        chooseCourse.setCompanyId(coursePublish.getCompanyId());//机构id
        chooseCourse.setOrderType(orderType);//免费课程
        chooseCourse.setStatus(status);//选课成功
        chooseCourse.setValidDays(365);//免费课程默认有效期365天
        chooseCourse.setValidtimeStart(LocalDateTime.now());//免费课程有效期开始时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//免费课程有效期结束时间
        int insertChooseCourse = chooseCourseMapper.insert(chooseCourse);
        if (insertChooseCourse < 1) {
            XcException.cast("添加免费选课记录失败");
        }

        //3.添加课程信息至用户课程表
        this.addCourseTables(chooseCourse);

        return chooseCourse;

    }


    @Transactional
    @Override
    public XcCourseTables addCourseTables(XcChooseCourse chooseCourse) {

        //1.选课成功可以添加到用户课程表
        String status = chooseCourse.getStatus();
        String success = Dictionary.ChooseCourse.Status_SUCCESS.getCode();
        if (!success.equals(status)){
            XcException.cast("选课未成功,无法添加到课程表");
        }

        //2.查询用户课程表
        String userId = chooseCourse.getUserId();
        Long courseId = chooseCourse.getCourseId();
        XcCourseTables xcCourseTables = courseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>()
                .eq(XcCourseTables::getUserId, userId)
                .eq(XcCourseTables::getCourseId, courseId));
        Long chooseCourseId = chooseCourse.getId();
        LocalDateTime newValidStart = chooseCourse.getValidtimeStart();
        LocalDateTime newValidEnd = chooseCourse.getValidtimeEnd();
        String orderType = chooseCourse.getOrderType();
        //2.1.课程已存在
        if(xcCourseTables != null) {
            //如果用户课程表中的过期时间比新订单的过期时间靠前,更新用户课程表
            if (xcCourseTables.getValidtimeEnd().isBefore(newValidEnd)) {
                xcCourseTables.setChooseCourseId(chooseCourseId);
                xcCourseTables.setUpdateDate(LocalDateTime.now());
                xcCourseTables.setValidtimeStart(newValidStart);
                xcCourseTables.setValidtimeEnd(newValidEnd);
                xcCourseTables.setCourseType(orderType);
                int update = courseTablesMapper.updateById(xcCourseTables);
                if (update < 1) {
                    XcException.cast("更新用户课程表失败");
                }
            }
            return xcCourseTables;
        }
        //2.2.课程不存在
        XcCourseTables xcCourseTablesNew = new XcCourseTables();
        xcCourseTablesNew.setChooseCourseId(chooseCourseId);
        xcCourseTablesNew.setUserId(userId);
        xcCourseTablesNew.setCourseId(courseId);
        xcCourseTablesNew.setCompanyId(chooseCourse.getCompanyId());
        xcCourseTablesNew.setCourseName(chooseCourse.getCourseName());
        xcCourseTablesNew.setValidtimeStart(newValidStart);
        xcCourseTablesNew.setValidtimeEnd(newValidEnd);
        xcCourseTablesNew.setCourseType(orderType);
        int insert = courseTablesMapper.insert(xcCourseTablesNew);
        if (insert < 1) {
            XcException.cast("添加用户课程表记录失败");
        }
        return xcCourseTablesNew;

    }


    @Transactional
    @Override
    public XcChooseCourse addPaidCourse(String userId, CoursePublish coursePublish){
        //1.查询待添加的收费待支付选课是否存在
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        String orderType = Dictionary.ChooseCourse.OrderType_PAID.getCode();//收费课程
        String status = Dictionary.ChooseCourse.Status_TO_BE_PAID.getCode();//选课状态待支付
        Long coursePublishId = coursePublish.getId();
        queryWrapper = queryWrapper.eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursePublishId)
                .eq(XcChooseCourse::getOrderType, orderType)
                .eq(XcChooseCourse::getStatus, status);
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }

        //2.添加收费选课记录
        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCourseId(coursePublishId);
        xcChooseCourse.setCourseName(coursePublish.getName());
        xcChooseCourse.setCoursePrice(coursePublish.getPrice());
        xcChooseCourse.setCompanyId(coursePublish.getCompanyId());
        xcChooseCourse.setOrderType(orderType);
        xcChooseCourse.setStatus(status);
        xcChooseCourse.setValidDays(coursePublish.getValidDays());
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursePublish.getValidDays()));
        int insert = chooseCourseMapper.insert(xcChooseCourse);
        if (insert < 1) {
            XcException.cast("添加收费选课记录失败");
        }
        return xcChooseCourse;

    }

    @Override
    public PageResult<XcCourseTables> listCourseTables(CourseTableParams params) {
        //页码
        long pageNo = params.getPage();
        //每页记录数,固定为4
        long pageSize = 4;
        //分页条件
        Page<XcCourseTables> page = new Page<>(pageNo, pageSize);
        //拼接查询条件
        //根据用户id查询
        String userId = params.getUserId();
        LambdaQueryWrapper<XcCourseTables> queryWrapper = new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId,
                userId);

        //分页查询
        Page<XcCourseTables> pageResult = courseTablesMapper.selectPage(page, queryWrapper);
        List<XcCourseTables> records = pageResult.getRecords();
        //记录总数
        long total = pageResult.getTotal();
        PageResult<XcCourseTables> courseTablesResult = new PageResult<>(records, total, pageNo, pageSize);
        return courseTablesResult;
    }




}

