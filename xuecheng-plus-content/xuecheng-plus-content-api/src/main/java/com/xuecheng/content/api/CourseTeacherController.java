package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 课程师资控制器
* @author TMC
* @date 2023/2/8 14:57
* @version 1.0
*/
@Api(value = "课程师资控制器", tags = {"课程师资控制器"})
@RestController
public class CourseTeacherController {

    @Resource
    CourseTeacherService courseTeacherService;

    @ApiOperation("课程师资列表查询接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> queryCourseTeacherList(@PathVariable Long courseId) {
        return courseTeacherService.queryCourseTeacherList(courseId);
    }

    @ApiOperation("保存课程教师信息接口")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody @Validated SaveTeacherDto saveTeacherDto) {
        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        return courseTeacherService.saveCourseTeacher(companyId, saveTeacherDto);
    }

    @ApiOperation("删除课程教师接口")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        courseTeacherService.deleteCourseTeacher(companyId, courseId, teacherId);
    }
}
