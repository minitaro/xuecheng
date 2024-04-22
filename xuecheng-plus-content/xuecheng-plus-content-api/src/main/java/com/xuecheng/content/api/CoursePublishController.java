package com.xuecheng.content.api;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.model.dto.CourseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 课程发布控制器
* @author TMC
* @date 2023/2/16 16:02
* @version 1.0
*/
@Api(value = "课程发布控制器", tags = {"课程发布控制器"})
@Controller
public class CoursePublishController {

    @Resource
    CoursePublishService coursePublishService;

    @ApiOperation(value = "预览课程接口")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView previewCourse(@PathVariable("courseId")  Long courseId) {

        CoursePreviewDto coursePreviewDto = coursePublishService.previewCourse(courseId);

        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据
        modelAndView.addObject("model", coursePreviewDto);
        //设置模板名称
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ApiOperation(value = "课程提交审核接口")
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {

        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        coursePublishService.commitAudit(companyId, courseId);

    }

    @ApiOperation(value = "课程发布接口")
    @PostMapping("/coursepublish/{courseId}")
    public void publishCourse(@PathVariable("courseId") Long courseId) {

        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        coursePublishService.publishCourse(companyId, courseId);

    }

    @ApiOperation(value = "查询课程发布信息接口")
    @GetMapping("/r/coursepublish/{courseId}")
    @ResponseBody
    @Cacheable(cacheNames = "course-publish", key = "'course_' + #courseId")
    public CoursePublish queryCoursePublish(@PathVariable("courseId") Long courseId) {

        return coursePublishService.getCoursePublish(courseId);

    }

    @ApiOperation("获取课程发布信息接口")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    @Cacheable(cacheNames = "course-preview", key = "'course_' + #courseId")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId) {

        //查询课程发布信息
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        if(coursePublish == null){
            return new CoursePreviewDto();
        }

        //基本信息和营销信息
        CourseInfoDto courseBase  = new CourseInfoDto();
        BeanUtils.copyProperties(coursePublish, courseBase);

        //课程计划
        String teachplanJson = coursePublish.getTeachplan();
        List<TeachplanDto> teachplanDtos = JSON.parseArray(teachplanJson, TeachplanDto.class);

        //教师信息
        String teachersJson = coursePublish.getTeachers();
        CourseTeacher courseTeacher = JSON.parseObject(teachersJson, CourseTeacher.class);

        //要封装的对象
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setTeachplans(teachplanDtos); //封装教学计划信息
        coursePreviewDto.setCourseBase(courseBase); //封装基本信息和营销信息
        coursePreviewDto.setCourseTeacher(courseTeacher); //封装教师信息

        return coursePreviewDto;

    }
}
