package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
* @description 课程分类树型结点响应dto
* @author TMC
* @date 2023/2/6 18:44
* @version 1.0
*/
@Data
@ToString
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    List childrenTreeNodes;

}
