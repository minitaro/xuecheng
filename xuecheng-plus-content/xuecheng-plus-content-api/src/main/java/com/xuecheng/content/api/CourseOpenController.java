package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 课程公开控制器
* @author TMC
* @date 2023/2/17 9:52
* @version 1.0
*/
@Api(value = "课程公开控制器", tags = "课程公开控制器")
@RestController
@RequestMapping("/open")
public class CourseOpenController{

    @Resource
    CoursePublishService coursePublishService;

    @ApiOperation(value = "预览课程接口")
    @GetMapping("/course/whole/{courseId}")
    @Cacheable(cacheNames = "course-preview", key = "'course_' + #courseId")
    public CoursePreviewDto previewCourse(@PathVariable("courseId") Long courseId) {

        return coursePublishService.previewCourse(courseId);

    }


}
