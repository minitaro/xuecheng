package com.xuecheng.learning.service;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.model.dto.CourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description 用户课程表服务接口
 * @author TMC
 * @date 2023/2/24 22:01
 * @version 1.0
 */
public interface CourseTableService {

    /**
     * @description 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     * @author TMC
     * @date 2023/2/24 22:01
     */
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @description 查询选课学习资格
     * @param userId
     * @param courseId
     * @return java.lang.String
     * @author TMC
     * @date 2023/2/28 13:23
     */
    XcCourseTablesDto queryCourseAccess(String userId, Long courseId);


    /**
     * @description 添加免费选课
     * @param userId 用户id
     * @param coursePublish 课程发布信息
     * @return com.xuecheng.learning.model.po.XcChooseCourse
     * @author TMC
     * @date 2023/2/28 13:05
     */
    @Transactional
    XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish);

    /**
    * @description 选课信息添加到用户课程表
    * @param chooseCourse 选课信息
    * @return com.xuecheng.learning.model.po.XcCourseTables
    * @author TMC
    * @date 2023/3/1 14:42
    */
    @Transactional
    XcCourseTables addCourseTables(XcChooseCourse chooseCourse);

    /**
     * @description 添加收费选课
     * @param userId 用户id
     * @param coursePublish 课程发布信息
     * @return com.xuecheng.learning.model.po.XcChooseCourse
     * @author TMC
     * @date 2023/2/28 14:36
     */
    @Transactional
    XcChooseCourse addPaidCourse(String userId, CoursePublish coursePublish);

    /**
    * @description 查询用户课程表
    * @param params 课程表查询请求参数
    * @return com.xuecheng.base.model.PageResult<com.xuecheng.learning.model.po.XcCourseTables>
    * @author TMC
    * @date 2023/2/28 19:59
    */
    PageResult<XcCourseTables> listCourseTables(CourseTableParams params);

}

