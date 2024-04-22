package com.xuecheng.orders.constants;

/**
 * @author TMC
 * @version 1.0
 * @description 支付宝API参数
 * @date 2023/2/28 2:40
 */
public class AlipayParam {

    public static final String ALIPAY_DATA_DATASERVICE_BILL_DOWNLOADURL_QUERY_RESPONSE =
            "alipay_data_dataservice_bill_downloadurl_query_response"; //对账单下载地址查询响应
    public static final String ALIPAY_TRADE_QUERY_RESPONSE = "alipay_trade_query_response"; //支付宝交易查询响应
    public static final String ALIPAY_TRADE_REFUND_RESPONSE = "alipay_trade_refund_response"; //支付宝交易退款响应
    public static final String APP_ID = "app_id"; //应用注册ID
    public static final String BILL_DATE = "bill_date"; //账单日期
    public static final String BILL_DOWNLOAD_URL = "bill_download_url"; //账单下载地址
    public static final String BILL_TYPE = "bill_type"; //账单类型
    public static final String G00DS_DETAIL = "goods_detail"; //订单包含的商品列表信息
    public static final String GOODS_ID = "goods_id"; //商品编号
    public static final String GOODS_NAME = "goods_name"; //商品名称
    public static final String OUT_REFUND_NO = "out_refund_no"; //商户退款单号
    public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
    public static final String PRICE = "price"; //商品单价
    public static final String PRODUCT_CODE = "product_code"; //销售产品码
    public static final String QUANTITY = "quantity"; //商品数量
    public static final String REFUND_AMOUNT = "refund_amount"; //退款金额
    public static final String REFUND_REASON = "refund_reason"; //退款原因说明
    public static final String SELLER_ID = "seller_id"; //卖家支付宝账号
    public static final String SUBJECT = "subject"; //订单标题
    public static final String TRADE_NO = "trade_no"; //支付宝交易号
    public static final String TRADE_STATUS = "trade_status"; //交易状态
    public static final String TOTAL_AMOUNT = "total_amount"; //订单总金额

    public AlipayParam() {
    }
}
