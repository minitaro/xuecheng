package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
* @description 测试:课程分类服务
* @author TMC
* @date 2023/3/2 21:08
* @version 1.0
*/
@SpringBootTest
public class CourseCategoryServiceTest {

    @Autowired
    CourseCategoryService courseCategoryService;

    /**
     * @description 测试:查询课程分类
     * @return void
     * @author TMC
     * @date 2023/2/6 20:18
     */
    @Test
    void queryTreeNodes() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
