package com.xuecheng.orders.constants;
/**
* @description 支付宝API数值
* @author TMC
* @date 2023/2/28 2:42
* @version 1.0
*/
public class AlipayValue {

    public static final String FAST_INSTANT_TRADE_PAY = "FAST_INSTANT_TRADE_PAY"; //销售产品码:快速交易支付
    public static final String QUICK_WAP_WAY = "QUICK_WAP_WAY"; //销售产品码:快速网站途径
    public static final String REFUND_ERROR = "REFUND_ERROR"; //退款出错
    public static final String REFUND_SUCCESS = "REFUND_SUCCESS"; //退款成功
    public static final String TRADE_CLOSED = "TRADE_CLOSED"; //交易关闭
    public static final String TRADE_FINISHED = "TRADE_FINISHED"; //交易完结
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS"; //交易成功
    public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY"; //等待支付

    public AlipayValue() {
    }
}
