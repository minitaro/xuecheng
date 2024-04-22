package com.xuecheng.search.controller;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.search.dto.SearchCourseParamDto;
import com.xuecheng.search.dto.SearchPageResultDto;
import com.xuecheng.search.po.CourseIndex;
import com.xuecheng.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 课程搜索控制器
* @author TMC
* @date 2023/2/18 12:22
* @version 1.0
*/
@Api(value = "课程搜索控制器", tags = "课程搜索控制器")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

    @Resource
    CourseSearchService courseSearchService;

    @ApiOperation("课程搜索列表接口")
    @GetMapping("/list")
    public SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto){

        if (searchCourseParamDto == null) {
            searchCourseParamDto = new SearchCourseParamDto();
        }
        return courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);

    }
}
