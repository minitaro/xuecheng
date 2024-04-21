package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
* @description 课程分类服务
* @author TMC
* @date 2023/2/6 18:58
* @version 1.0
*/
public interface CourseCategoryService {

    /**
    * @description 查询课程分类
    * @param id 课程分类根节点id
    * @return java.util.List<com.xuecheng.content.model.dto.CourseCategoryTreeDto>
    * @author TMC
    * @date 2023/3/2 20:34
    */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
