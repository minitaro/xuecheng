package com.xuecheng.search.controller;

import com.xuecheng.search.po.CourseIndex;
import com.xuecheng.search.service.IndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 课程索引控制器
* @author TMC
* @date 2023/2/18 11:30
* @version 1.0
*/
@Api(value = "课程索引控制器", tags = "课程索引控制器")
@RestController
@RequestMapping("/index")
public class CourseIndexController {

    @Value("${elasticsearch.course.index}")
    private String index;
    @Resource
    IndexService indexService;

    @ApiOperation("添加课程索引接口")
    @PostMapping("/course")
    public Boolean addIndex(@RequestBody @Validated CourseIndex courseIndex) {

        String id = String.valueOf(courseIndex.getId());
        return indexService.addIndex(index, id, courseIndex);

    }
}
