package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
* @description 课程分类树形结构查询业务接口
* @author TMC
* @date 2023/2/6 18:58
* @version 1.0
*/
public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
