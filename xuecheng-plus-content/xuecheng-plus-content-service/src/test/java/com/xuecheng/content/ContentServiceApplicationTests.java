package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
* @description 内容管理模块业务层测试
* @author TMC
* @date 2023/2/6 10:06
* @version 1.0
*/
@SpringBootTest
public class ContentServiceApplicationTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    CourseCategoryService courseCategoryService;

    /**
    * @description DAO层CourseBaseMapper接口测试
    *
    * @return void
    * @author TMC
    * @date 2023/2/6 11:20
    */
    @Test
    void testCourseBaseMapper() {

        CourseBase courseBase = courseBaseMapper.selectById(74L);
        Assertions.assertNotNull(courseBase);

    }

    /**
    * @description 业务层CourseBaseInfoService接口测试
    * @return
    * @author TMC
    * @date 2023/2/6 11:22
    */
    @Test
    void testCourseBaseInfoService() {
        PageParams pageParams = new PageParams(1L,1L);
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams,
                new QueryCourseParamsDto());
        System.out.println(courseBasePageResult);
    }

    /**
    * @description 业务层CourseCategoryService接口测试
    * @return void
    * @author TMC
    * @date 2023/2/6 20:18
    */
    @Test
    void testCourseCategoryService() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
