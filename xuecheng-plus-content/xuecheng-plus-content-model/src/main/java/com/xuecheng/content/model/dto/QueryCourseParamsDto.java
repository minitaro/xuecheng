package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
* @description 课程查询请求dto
* @author TMC
* @date 2023/2/5 16:57
* @version 1.0
*/
@ApiModel(value="QueryCourseParamsDto", description="课程查询条件")
@Data
@ToString
public class QueryCourseParamsDto {

    /**
     * 审核状态
     */
    @ApiModelProperty("审核状态")
    private String auditStatus;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 发布状态
     */
    @ApiModelProperty("发布状态")
    private String publishStatus;
}
