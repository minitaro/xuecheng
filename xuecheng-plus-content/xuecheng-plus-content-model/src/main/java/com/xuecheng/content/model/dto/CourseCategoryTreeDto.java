package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
* @description 课程分类响应dto
* @author TMC
* @date 2023/2/6 18:44
* @version 1.0
*/
@ApiModel(value="CourseCategoryTreeDto", description="课程分类")
@Data
@ToString
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    List<CourseCategoryTreeDto> childrenTreeNodes;

}
