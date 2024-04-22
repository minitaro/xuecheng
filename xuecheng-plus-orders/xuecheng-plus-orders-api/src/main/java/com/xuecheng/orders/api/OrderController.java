package com.xuecheng.orders.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.orders.config.AlipayClientConfig;
import com.xuecheng.orders.constants.AlipayParam;
import com.xuecheng.orders.constants.AlipayValue;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import com.xuecheng.orders.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
* @description 订单控制器
* @author TMC
* @date 2023/2/28 22:08
* @version 1.0
*/
@Api(value = "订单控制器", tags = "订单控制器")
@Slf4j
@Controller
public class OrderController {

    @Resource
    OrderService orderService;

    @Resource
    AlipayClient alipayClient;

    @Resource
    AlipayClientConfig alipayClientConfig;


    @ApiOperation("生成支付二维码接口")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody @Validated AddOrderDto addOrderDto) {
        String userId = SecurityUtil.getUser().getId();
        return orderService.createOrder(userId, addOrderDto);
    }

    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestpay(String payNo, HttpServletResponse httpResponse) throws IOException {
        //1.校验payNo支付记录交易号是否存在
        XcPayRecord payRecord = orderService.getPayRecordByPayno(payNo);
        if (payRecord == null) {
            XcException.cast("支付记录交易号不存在");
        }
        //2.创建支付请求
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();//创建API对应的request
        request.setNotifyUrl(alipayClientConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.put(AlipayParam.OUT_TRADE_NO, payRecord.getPayNo());
        bizContent.put(AlipayParam.TOTAL_AMOUNT, payRecord.getTotalPrice());
        bizContent.put(AlipayParam.SUBJECT, payRecord.getOrderName());
        bizContent.put(AlipayParam.PRODUCT_CODE, AlipayValue.QUICK_WAP_WAY);
        request.setBizContent(bizContent.toString());
        //3.发送请求
        AlipayTradeWapPayResponse response = null;
        try {
            response = alipayClient.pageExecute(request);
        } catch (Exception e) {
            XcException.cast("创建支付交易失败");
        }
        //4.封装响应表单返回
        if(response.isSuccess()){
            log.info("调用成功，返回结果 ===> " + response.getBody());
            String form = response.getBody();
            httpResponse.setContentType("text/html;charset=" + AlipayConstants.CHARSET_UTF8);
            httpResponse.getWriter().write(form);//直接将完整的支付表单html输出到页面
            httpResponse.getWriter().flush();
        } else {
            log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
            XcException.cast("创建支付交易失败");
        }
    }

    @ApiOperation("接收支付通知接口")
    @RequestMapping("/receivenotify")
    public void tradeNotify(@RequestParam Map<String, String> params, HttpServletResponse response) {
        log.info("支付通知正在执行");
        log.info("通知参数 ===> {}", params);
        String result = "failure";
        try {
            //1.异步通知验签
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayClientConfig.getAlipayPublicKey(),
                    AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2); //调用SDK验证签名
            if (!signVerified) {
                //1.1.验签失败则记录异常日志,并在response中返回failure.
                log.error("支付成功异步通知验签失败!");
                response.getWriter().println(result);
            }
            //1.2.验签成功,按照支付结果异步通知中的描述,对支付结果中的业务内容进行二次校验
            log.info("支付成功异步通知验签成功!");

            //2.验证 out_trade_no 是否为商户系统中创建的支付流水号
            String outTradeNo = params.get(AlipayParam.OUT_TRADE_NO);
            XcPayRecord payRecord = orderService.getPayRecordByPayno(outTradeNo);
            if (payRecord == null) {
                log.error("支付记录不存在");
                response.getWriter().println(result);
            }
            //3.判断 total_amount 是否确实为该订单的实际金额 (即商户订单创建时的金额)
            String totalAmount = params.get(AlipayParam.TOTAL_AMOUNT);
            int total = (int) (Float.parseFloat(totalAmount) * 100);
            int totalPrice = (int) (payRecord.getTotalPrice() * 100);
            if (total != totalPrice) {
                log.error("金额校验失败");
                response.getWriter().println(result);
            }
            //4.校验 seller_id 是否为 out_trade_no 这笔单据的对应的操作方
            String sellerId = params.get(AlipayParam.SELLER_ID);
            String sellerIdProperty = alipayClientConfig.getSellerId();
            if (!sellerIdProperty.equals(sellerId)) {
                log.error("商家pid校验失败");
                response.getWriter().println(result);
            }
            //5.验证 app_id 是否为该商户本身
            String appId = params.get(AlipayParam.APP_ID);
            String appIdProperty = alipayClientConfig.getAppId();
            if (!appIdProperty.equals(appId)) {
                log.error("appid校验失败");
                response.getWriter().println(result);
            }
            //6.校验交易状态:在支付宝的业务通知中,只有交易通知状态为TRADE_SUCCESS时,支付宝才会认定为买家付款成功。
            String tradeStatus = params.get(AlipayParam.TRADE_STATUS);
            if (!AlipayValue.TRADE_SUCCESS.equals(tradeStatus) && !AlipayValue.TRADE_FINISHED.equals(tradeStatus)) {
                log.error("校验交易状态为未成功");
                response.getWriter().println(result);
            }
            //7.处理业务:修改订单状态,记录支付日志
            PayStatusDto payStatusDto = new PayStatusDto();
            payStatusDto.setApp_id(appId);
            payStatusDto.setTrade_status(tradeStatus);
            payStatusDto.setOut_trade_no(outTradeNo);
            payStatusDto.setTotal_amount(totalAmount);
            payStatusDto.setTrade_no(params.get(AlipayParam.TRADE_NO));
            orderService.saveAlipayStatus(payStatusDto);

            //8.校验成功后在response中返回success并继续商户自身业务处理,校验失败返回failure
            result = "success";
            response.getWriter().println(result);
        } catch (Exception e) {
            log.error("校验支付通知出错:{}", e.getMessage());
            XcException.cast("校验支付通知出错");
        }

    }



}
