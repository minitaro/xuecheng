package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseInfoService;
import com.xuecheng.content.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description 课程信息控制器
 * @author TMC
 * @date 2023/2/5 17:35
 * @version 1.0
 */
@Api(value = "课程信息控制器", tags = {"课程信息控制器"})
@RestController
public class CourseInfoController {

    @Resource
    CourseInfoService courseInfoService;

    @ApiOperation("课程列表查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    @PostMapping("/course/list")
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams,
                                                      @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        return courseInfoService.queryCourseBaseList(companyId, pageParams, queryCourseParamsDto);
    }

    @ApiOperation("新增课程接口")
    @PostMapping("/course")
    public CourseInfoDto addCourse(@RequestBody @Validated AddCourseDto addCourseDto) {
        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        return courseInfoService.addCourse(companyId, addCourseDto);
    }

    @ApiOperation("根据id查询课程接口")
    @GetMapping("/course/{courseId}")
    public CourseInfoDto getCourseInfoById(@PathVariable Long courseId) {

        return courseInfoService.getCourseInfoById(courseId);
    }

    @ApiOperation("修改课程接口")
    @PutMapping("/course")
    public CourseInfoDto editCourse(@RequestBody @Validated EditCourseDto editCourseDto) {
        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        return courseInfoService.editCourse(companyId, editCourseDto);
    }

    @ApiOperation("根据id删除课程接口")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourseById(@PathVariable Long courseId) {
        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        courseInfoService.deleteCourseById(companyId, courseId);
    }


}
