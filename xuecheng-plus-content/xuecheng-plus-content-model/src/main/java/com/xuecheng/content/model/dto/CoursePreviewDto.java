package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
* @description 课程预览响应dto
* @author TMC
* @date 2023/2/16 16:12
* @version 1.0
*/
@Data
@ToString
public class CoursePreviewDto {

    //课程基本信息,营销信息
    CourseInfoDto courseBase;

    //课程计划信息
    List<TeachplanDto> teachplans;

    //师资信息
    CourseTeacher courseTeacher;
}
