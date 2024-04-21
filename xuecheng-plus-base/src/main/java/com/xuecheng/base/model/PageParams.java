package com.xuecheng.base.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
* @description 分页查询请求模型类
* @author TMC
* @date 2023/2/5 16:44
* @version 1.0
*/
@ApiModel(value = "PageParams", description = "分页查询请求")
@Data
@ToString
@AllArgsConstructor
public class PageParams {

    /**
     * 当前页码默认值
     */
    public static final long DEFAULT_PAGE_CURRENT = 1L;

    /**
     * 每页记录数默认值
     */
    public static final long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 当前页码
     */
    @ApiModelProperty("当前页码")
    private Long pageNo = DEFAULT_PAGE_CURRENT;

    /**
     * 每页记录数默认值
     */
    @ApiModelProperty("每页记录数")
    private Long pageSize = DEFAULT_PAGE_SIZE;

}
