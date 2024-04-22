package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

/**
* @description 订单服务
* @author TMC
* @date 2023/2/28 22:12
* @version 1.0
*/
public interface OrderService {

    /**
    * @description 创建订单
    * @param userId 用户id
     * @param addOrderDto 订单信息
    * @return com.xuecheng.orders.model.dto.PayRecordDto
    * @author TMC
    * @date 2023/2/28 22:13
    */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
    * @description 根据支付交易号查询支付记录
    * @param payNo 支付交易号
    * @return com.xuecheng.orders.model.po.XcPayRecord
    * @author TMC
    * @date 2023/3/8 0:53
    */
    XcPayRecord getPayRecordByPayno(String payNo);

    /**
    * @description 保存支付结果
    * @param payStatusDto 支付结果dto
    * @return void
    * @author TMC
    * @date 2023/3/8 1:23
    */
    void saveAlipayStatus(PayStatusDto payStatusDto);
}
