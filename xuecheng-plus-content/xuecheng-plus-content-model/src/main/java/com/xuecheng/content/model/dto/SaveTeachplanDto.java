package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
* @description 增改课程计划请求dto
* @author TMC
* @date 2023/2/8 11:44
* @version 1.0
*/
@ApiModel(value="SaveTeachplanDto", description="保存课程计划")
@Data
@ToString
public class SaveTeachplanDto {

    /**
     * 课程计划id
     */
    @ApiModelProperty(value = "课程计划id")
    private Long id;

    /**
     * 课程计划名称
     */
    @ApiModelProperty(value = "课程计划名称")
    @NotBlank(message = "课程计划名称不能为空")
    private String pname;

    /**
     * 课程计划父级id
     */
    @ApiModelProperty(value = "课程计划父级id")
    @NotNull(message = "课程计划父级id不能为空")
    @PositiveOrZero(message = "课程计划父级id要求大于等于0")
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    @ApiModelProperty(value = "课程计划层级")
    @NotNull(message = "课程计划层级不能为空")
    @Range(min = 1L, max = 3L)
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    @ApiModelProperty(value = "课程类型")
    private String mediaType;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    @NotNull(message = "课程id不能为空")
    @Min(value = 1L, message = "课程id要求大于等于1")
    private Long courseId;

    /**
     * 课程发布id
     */
    @ApiModelProperty(value = "课程发布id")
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    @ApiModelProperty(value = "是否支持试学或预览（试看）")
    private String isPreview;
}
