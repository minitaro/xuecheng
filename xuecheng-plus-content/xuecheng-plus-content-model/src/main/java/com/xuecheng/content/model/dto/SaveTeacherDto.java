package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @description 保存教师信息请求dto
* @author TMC
* @date 2023/3/4 1:24
* @version 1.0
*/
@ApiModel(value="SaveTeacherDto", description="保存教师信息")
@Data
@ToString
public class SaveTeacherDto {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    @NotNull(message = "课程id不能为空")
    @Min(value = 1L, message = "课程id要求大于等于1")
    private Long courseId;

    /**
     * 教师姓名
     */
    @ApiModelProperty(value = "教师姓名")
    @NotBlank(message = "教师姓名不能为空")
    private String teacherName;

    /**
     * 教师职位
     */
    @ApiModelProperty(value = "教师职位")
    @NotBlank(message = "教师职位不能为空")
    private String position;

    /**
     * 教师简介
     */
    @ApiModelProperty(value = "教师简介")
    private String introduction;

    /**
     * 照片
     */
    @ApiModelProperty(value = "照片")
    private String photograph;



}
