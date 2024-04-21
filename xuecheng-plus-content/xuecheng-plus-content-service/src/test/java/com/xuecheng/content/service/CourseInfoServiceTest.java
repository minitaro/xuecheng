package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
* @description 测试:课程信息服务
* @author TMC
* @date 2023/2/6 10:06
* @version 1.0
*/
@SpringBootTest
public class CourseInfoServiceTest {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseInfoService courseBaseInfoService;

    @Autowired
    CourseCategoryService courseCategoryService;



    /**
    * @description 测试:查询课程基本信息列表
    * @return
    * @author TMC
    * @date 2023/2/6 11:22
    */
    @Test
    void queryCourseBaseList() {
        PageParams pageParams = new PageParams(1L,1L);
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(22L, pageParams,
                new QueryCourseParamsDto());
        System.out.println(courseBasePageResult);
    }




    @Test
    void testCreateCourse() {
        AddCourseDto addCourseDto = new AddCourseDto();
        addCourseDto.setMt("1-1");
        addCourseDto.setSt("1-1-1");
        addCourseDto.setName("测试课程113");
        addCourseDto.setPic("");
        addCourseDto.setTeachmode("200002");
        addCourseDto.setUsers("初级人员");
        addCourseDto.setTags("标签");
        addCourseDto.setGrade("204001");
        addCourseDto.setDescription("课程介绍");
        addCourseDto.setCharge("201001");
        addCourseDto.setQq("43232");
        addCourseDto.setWechat("4324322");
        addCourseDto.setPhone("432432");
        addCourseDto.setValidDays(365);
        CourseInfoDto courseBase = courseBaseInfoService.addCourse(22L, addCourseDto);
        System.out.println(courseBase);
    }
}
