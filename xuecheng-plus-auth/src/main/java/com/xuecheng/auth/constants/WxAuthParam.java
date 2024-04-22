package com.xuecheng.auth.constants;
/**
* @description 微信授权API参数
* @author TMC
* @date 2023/3/6 22:46
* @version 1.0
*/
public class WxAuthParam {

    public static final String UNIONID = "unionid";
    public static final String NICKNAME = "nickname";
    public static final String HEADIMGURL = "headimgurl";
    public static final String OPENID = "openid";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ERRMSG = "errmsg";
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret" +
            "=%s&code=%s&grant_type=authorization_code";
    public static final String USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";


}
