package com.xuecheng.orders.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @description 添加订单请求dto
* @author TMC
* @date 2023/3/7 21:31
* @version 1.0
*/
@ApiModel(value = "AddOrderDto", description = "添加订单请求")
@Data
@ToString
public class AddOrderDto {

    /**
     * 总价
     */
    @ApiModelProperty(value = "总价", required = true)
    @NotNull(message = "订单总价不能为空")
    private Float totalPrice;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型", required = true)
    @NotBlank(message = "订单类型不能为空")
    private String orderType;

    /**
     * 订单名称
     */
    @ApiModelProperty(value = "订单名称", required = true)
    @NotBlank(message = "订单名称不能为空")
    private String orderName;

    /**
     * 订单描述
     */
    @ApiModelProperty(value = "订单描述", required = true)
    private String orderDescrip;

    /**
     * 订单明细json，不可为空
     * [{"goodsId":"","goodsType":"","goodsName":"","goodsPrice":"","goodsDetail":""},{...}]
     */
    @ApiModelProperty(value = "订单明细", required = true)
    @NotBlank(message = "订单明细不能为空")
    private String orderDetail;

    /**
     * 外部系统业务id
     */
    @ApiModelProperty(value = "外部系统业务id", required = true)
    @NotBlank(message = "外部系统业务id不能为空")
    private String outBusinessId;
}
