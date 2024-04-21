package com.xuecheng.learning.api;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.CourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.CourseTableService;
import com.xuecheng.learning.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 用户课程表控制器
 * @author TMC
 * @date 2023/2/24 21:56
 * @version 1.0
 */
@Api(value = "用户课程表控制器", tags = "用户课程表控制器")
@RestController
public class CourseTableController {

    @Resource
    CourseTableService courseTableService;

    @ApiOperation("添加选课接口")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId)  {
        String userId = SecurityUtil.getUser().getId();
        return  courseTableService.addChooseCourse(userId, courseId);
    }

    @ApiOperation("查询选课学习资格接口")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto queryCourseAccess(@PathVariable("courseId") Long courseId) {
        String userId = SecurityUtil.getUser().getId();
        return courseTableService.queryCourseAccess(userId, courseId);
    }

    @ApiOperation("课程表查询接口")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> listCourseTables(CourseTableParams params) {
        String userId = SecurityUtil.getUser().getId();
        params.setUserId(userId);
        return courseTableService.listCourseTables(params);
    }



}
