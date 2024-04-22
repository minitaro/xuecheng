package com.xuecheng.orders.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;

/**
* @description TODO
* @author TMC
* @date 2023/3/1 1:01
* @version 1.0
*/
public class test {

        public static void main(String[] args) throws AlipayApiException {
            String privateKey = "4mNAxdPBlkppV0eaqV2eO8X+On6IOQNAtvOPpShA5Z7JAn9iy3fXHbAv58FM9emfPGQJOTG4wrjDHzkixn9t2IKSSWXKVRc/OOV+G9JxIzojW8wkIqj6QnG93MHc3/0IXwdkXt93PG2y8XMvl/+OdtX0D1mXyl1J8bY4OVjm3adMc8uUMcgoU7sHP5io9BPqtXNfjvvvxIATpCl12K0uBMyX";
            String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwNFSPtAfgUeek/j3Q7Lv3mU5qefBkQWpKnruQThN3lC7JfPZf0D874Naee54IgGO9DhDv/xmU4OOq9kZjm4BmMLB4BBAPDdvUGofTnZD5ppriJkBx8prrr+a71p5VxsXwPcNznhOp/VIQeYf4xVVxYi0SGSAgOKfGSjAvLPxfoQF753GAF32XZU/2xJfpULrdT4iE/WiVjendWq7QSgPb5BbU5segfBLsZEESbXhjKGvWrTi9VidEmKwaU6EZk9S1cVkhWwN3GWSV368R94jXPGHgZHjJi+PAa0+3tOncqfUbka0iQfGsM4+rPVYFpYkxYD3k11Vquskw1vMnhUfbQIDAQAB";
            AlipayConfig alipayConfig = new AlipayConfig();
            alipayConfig.setServerUrl("https://openapi.alipaydev.com/gateway.do");
            alipayConfig.setAppId("2021000122617371");
            alipayConfig.setPrivateKey(privateKey);
            alipayConfig.setFormat("json");
            alipayConfig.setAlipayPublicKey(alipayPublicKey);
            alipayConfig.setCharset("UTF8");
            alipayConfig.setSignType("RSA2");
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setOutTradeNo("70501111111S001111119");
            model.setTotalAmount("9.00");
            model.setSubject("大乐透");
            model.setProductCode("QUICK_WAP_WAY");
            request.setBizModel(model);
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
            System.out.println(response.getBody());
            if (response.isSuccess()) {
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        }
    }


