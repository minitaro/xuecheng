package com.xuecheng.orders.model.dto;

import lombok.Data;
import lombok.ToString;

/**
* @description 支付结果dto
* @author TMC
* @date 2023/3/8 1:17
* @version 1.0
*/
@Data
@ToString
public class PayStatusDto {

    /**
     * 商户订单号
     */
    String out_trade_no;

    /**
     * 支付宝交易号
     */
    String trade_no;

    /**
     * 交易状态
     */
    String trade_status;

    /**
     * 应用id
     */
    String app_id;

    /**
     * 总金额
     */
    String total_amount;
}
