package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
* @description 课程师资服务
* @author TMC
* @date 2023/2/8 15:04
* @version 1.0
*/
public interface CourseTeacherService {

    /**
    * @description 根据课程id查询课程教师列表
    * @param courseId 课程id
    * @return java.util.List<com.xuecheng.content.model.po.CourseTeacher>
    * @author TMC
    * @date 2023/2/8 19:53
    */
    List<CourseTeacher> queryCourseTeacherList(Long courseId);

    /**
    * @description 保存教师信息
    * @param companyId 机构id
     * @param saveTeacherDto 教师信息
    * @return com.xuecheng.content.model.po.CourseTeacher
    * @author TMC
    * @date 2023/3/4 1:39
    */
    CourseTeacher saveCourseTeacher(Long companyId, SaveTeacherDto saveTeacherDto);

    /**
    * @description 删除课程教师
    * @param companyId 机构id
     * @param courseId 课程id
     * @param teacherId 教师id
    * @return void
    * @author TMC
    * @date 2023/2/9 17:20
    */
    void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId);
}
