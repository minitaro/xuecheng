package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.PayNotifyConfig;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author TMC
 * @version 1.0
 * @description 订单服务实现类
 * @date 2023/2/28 22:13
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    XcOrdersMapper ordersMapper;

    @Resource
    XcOrdersGoodsMapper ordersGoodsMapper;

    @Resource
    XcPayRecordMapper payRecordMapper;

    @Resource
    MqMessageService mqMessageService;

    @Transactional
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {

        //1.添加商品订单
        XcOrders orders = this.saveXcOrders(userId, addOrderDto);

        //2.添加支付记录
        XcPayRecord payRecord = this.createPayRecord(orders);

        //3.生成支付二维码
        String qrCode = null;
        try {
            // http://feb9d7.natappfree.cc为natapp提供的内网穿透映射地址
            qrCode =
                    new QRCodeUtil().createQRCode("http://feb9d7.natappfree.cc/api/orders/requestpay?payNo=" + payRecord.getPayNo(), 200, 200);
        } catch (IOException e) {
            XcException.cast("生成二维码出错");
        }

        //4.封装要返回的数据
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(qrCode);

        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        LambdaQueryWrapper<XcPayRecord> queryWrapper = new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo
                , payNo);
        return payRecordMapper.selectOne(queryWrapper);
    }

    @Transactional
    @Override
    public void saveAlipayStatus(PayStatusDto payStatusDto) {

        //1.更新支付记录
        //1.1.查询支付记录
        String payNo = payStatusDto.getOut_trade_no();
        XcPayRecord payRecord = this.getPayRecordByPayno(payNo);
        if (payRecord == null) {
            log.info("收到支付结果通知,查询不到支付记录,信息:{}", payStatusDto);
            return;
        }
        //1.2.校验支付状态
        String status = payRecord.getStatus();
        if (Dictionary.PayRecord.Status_SUCCESS.getCode().equals(status)) {
            log.info("收到支付结果通知,支付记录状态已经为支付成功,不进行任务操作");
            return;
        }
        //1.3.更新支付记录
        XcPayRecord payRecordNew = new XcPayRecord();
        payRecordNew.setStatus(Dictionary.PayRecord.Status_SUCCESS.getCode());//支付成功
        payRecordNew.setOutPayNo(payStatusDto.getTrade_no());//支付宝订单号
        payRecordNew.setOutPayChannel(Dictionary.PayRecord.OutPayChannel_ALIPAY.getCode());//支付方式:支付宝
        payRecordNew.setPaySuccessTime(LocalDateTime.now());
        LambdaUpdateWrapper<XcPayRecord> updateWrapper =
                new LambdaUpdateWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo,
                payNo);
        int updatePayRecord = payRecordMapper.update(payRecordNew, updateWrapper);
        if (updatePayRecord > 0) {
            log.info("更新支付记录成功:{}", payStatusDto);
        } else {
            log.info("更新支付记录失败:{}", payStatusDto);
        }

        //2.更新订单
        //2.1.查询订单
        Long orderId = payRecord.getOrderId();
        XcOrders orders = ordersMapper.selectById(orderId);
        if (orders == null) {
            log.info("订单不存在,支付记录:{},订单号:{}", payStatusDto, orderId);
            return;
        }
        //2.2.更新订单状态
        XcOrders ordersNew = new XcOrders();
        ordersNew.setStatus(Dictionary.Orders.Status_SUCCESS.getCode());//更新订单状态为支付成功
        int updateOrders = ordersMapper.update(ordersNew, new LambdaUpdateWrapper<XcOrders>().eq(XcOrders::getId, orderId));
        if (updateOrders > 0) {
            log.info("更新订单成功,支付记录:{},订单号:{}", payStatusDto, orderId);
            String outBusinessId = orders.getOutBusinessId();//订单表所关联的外部业务系统的主键
            //向消息表插入记录
            mqMessageService.addMessage(PayNotifyConfig.MESSAGE_TYPE, outBusinessId, orders.getOrderType(), null);
        } else {
            log.info("更新订单失败,支付记录:{},订单号:{}", payStatusDto, orderId);
        }

    }


    /**
     * @param orders 订单
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @description 创建支付记录
     * @author TMC
     * @date 2023/3/7 22:07
     */
    private XcPayRecord createPayRecord(XcOrders orders) {

        XcPayRecord payRecord = new XcPayRecord();
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);//支付记录交易号
        //记录关键订单id
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setStatus(Dictionary.PayRecord.Status_TO_BE_PAID.getCode());//未支付
        payRecord.setUserId(orders.getUserId());
        int insert = payRecordMapper.insert(payRecord);
        if (insert < 1) {
            XcException.cast("创建支付记录失败");
        }
        return payRecord;
    }

    /**
     * @param userId      用户id
     * @param addOrderDto 添加订单请求
     * @return com.xuecheng.orders.model.po.XcOrders
     * @description 添加订单
     * @author TMC
     * @date 2023/3/7 22:05
     */
    private XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto) {

        //1.订单插入幂等性处理
        String outBusinessId = addOrderDto.getOutBusinessId();
        XcOrders orders = ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId,
                outBusinessId));
        if (orders != null) {
            return orders;
        }

        //2.添加订单
        orders = new XcOrders();
        long orderId = IdWorkerUtils.getInstance().nextId();//订单号
        orders.setId(orderId);
        orders.setTotalPrice(addOrderDto.getTotalPrice());
        orders.setStatus(Dictionary.Orders.Status_TO_BE_PAID.getCode());//未支付
        orders.setUserId(userId);
        orders.setOrderType(addOrderDto.getOrderType());
        orders.setOrderName(addOrderDto.getOrderName());
        orders.setOrderDetail(addOrderDto.getOrderDetail());
        orders.setOrderDescrip(addOrderDto.getOrderDescrip());
        orders.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        int insertOrders = ordersMapper.insert(orders);
        if (insertOrders < 1) {
            XcException.cast("创建订单失败");
        }

        //3.添加订单明细
        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoods = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        xcOrdersGoods.forEach(ordersGoods -> {
            ordersGoods.setOrderId(orderId);//在明细中记录订单号
            int insertGoods = ordersGoodsMapper.insert(ordersGoods);
            if (insertGoods < 1) {
                XcException.cast("创建订单明细失败");
            }
        });

        return orders;
    }

}
