package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
* @description 修改课程请求dto
* @author TMC
* @date 2023/2/7 19:25
* @version 1.0
*/
@ApiModel(value="EditCourseDto", description="修改课程信息")
@Data
@ToString
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value = "课程id", required = true)
    @NotNull(message = "课程id不能为空")
    @Min(value = 1L, message = "课程id必须大于等于1")
    private Long id;

}
