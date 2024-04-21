package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
* @description 课程信息服务
* @author TMC
* @date 2023/2/6 10:34
* @version 1.0
*/
public interface CourseInfoService {
    

    /**
    * @description 查询课程基本信息列表
    * @param companyId 机构id
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 课程查询条件
    * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
    * @author TMC
    * @date 2023/2/20 19:05
    */
    PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams,
                                               QueryCourseParamsDto queryCourseParamsDto);

    /**
    * @description 新增课程
    * @param companyId 教学机构id
     * @param addCourseDto 待添加课程信息
    * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
    * @author TMC
    * @date 2023/2/7 12:15
    */
    CourseInfoDto addCourse(Long companyId, AddCourseDto addCourseDto);

    /**
     * @description 根据课程id查询课程信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author TMC
     * @date 2023/2/7 16:02
     */
    CourseInfoDto getCourseInfoById(Long courseId);

    /**
    * @description 修改课程信息
    * @param companyId 教学机构id
     * @param editCourseDto 待修改课程信息
    * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
    * @author TMC
    * @date 2023/2/7 19:47
    */
    CourseInfoDto editCourse(Long companyId, EditCourseDto editCourseDto);

    /**
    * @description 删除课程信息
    * @param companyId 机构id
     * @param courseId 课程id
    * @return void
    * @author TMC
    * @date 2023/2/9 16:51
    */
    void deleteCourseById(Long companyId, Long courseId);
}
