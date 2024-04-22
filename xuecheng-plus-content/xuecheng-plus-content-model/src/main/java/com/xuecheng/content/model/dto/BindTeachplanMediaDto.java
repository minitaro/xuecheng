package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @description 教学计划-媒资绑定请求dto
* @author TMC
* @date 2023/2/14 10:40
* @version 1.0
*/
@ApiModel(value="BindTeachplanMediaDto", description="教学计划-媒资绑定提交数据")
@Data
@ToString
public class BindTeachplanMediaDto {

    @ApiModelProperty(value = "媒资文件id", required = true)
    @NotBlank(message = "媒资文件id不能为空")
    private String mediaId;

    @ApiModelProperty(value = "媒资文件名称", required = true)
    @NotBlank(message = "媒资文件名称不能为空")
    private String fileName;

    @ApiModelProperty(value = "课程计划id", required = true)
    @NotNull(message = "课程计划id不能为空")
    private Long teachplanId;


}
