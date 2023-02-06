package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @description 课程信息管理业务接口实现类
* @author TMC
* @date 2023/2/6 10:38
* @version 1.0
*/
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //1.构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //1.1.构建查询条件,根据课程名称查询
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName,
                queryCourseParamsDto.getCourseName());
        //1.2.构建查询条件,根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus());
        //1.3.构建查询条件,根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getStatus()), CourseBase::getStatus,
                queryCourseParamsDto.getStatus());

        //2.构建分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //3.查询数据内容获取结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //4.查询结果封装
        //4.1.获取数据列表
        List<CourseBase> list = pageResult.getRecords();
        //4.2.获取数据总数
        long total = pageResult.getTotal();
        //4.3.构建结果集
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(list, total, pageParams.getPageNo(),
                pageParams.getPageSize());
        return courseBasePageResult;
    }
}
