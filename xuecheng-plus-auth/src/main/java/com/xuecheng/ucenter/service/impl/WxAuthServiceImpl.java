package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.auth.constants.WxAuthParam;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

/**
* @description 微信认证服务
* @author TMC
* @date 2023/2/20 11:44
* @version 1.0
*/
@Slf4j
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {

    @Value("${weixin.appid}")
    String appid;

    @Value("${weixin.secret}")
    String secret;

    @Resource
    RestTemplate restTemplate;

    @Resource
    XcUserMapper xcUserMapper;

    @Resource
    XcUserRoleMapper xcUserRoleMapper;


    @Transactional
    @Override
    public XcUserExt authenticate(AuthParamsDto authParamsDto) {

        //1.查询用户是否存在
        String username = authParamsDto.getUsername();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null) {
            XcException.cast("微信认证失败");
        }

        //2.封装返回用户信息
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        return xcUserExt;

    }

    /**
     * @param code  授权码
     * @param state 状态参数
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description 携带授权码获取微信用户信息
     * @author TMC
     * @date 2023/3/9 11:27
     */
    public XcUser userInfo(String code, String state) {

        //1.携带授权码申请获取令牌(access_token)
        Map<String,String> accessTokenMap = this.getAccessToken(code);
        if (accessTokenMap == null || accessTokenMap.get(WxAuthParam.ERRMSG) != null) {
            XcException.cast("微信认证失败");
        }

        //2.携带令牌申请获取用户信息
        String openid = accessTokenMap.get(WxAuthParam.OPENID);
        String access_token = accessTokenMap.get(WxAuthParam.ACCESS_TOKEN);
        Map<String,Object> userInfo = this.getUserInfo(access_token, openid);
        if (userInfo == null || userInfo.get(WxAuthParam.ERRMSG) != null) {
            XcException.cast("微信认证失败");
        }

        //3.检验用户是否注册
        String unionid = userInfo.get(WxAuthParam.UNIONID).toString();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        //3.1.用户未注册
        if(user == null){

            //保存用户信息至xc_user表
            String userId = UUID.randomUUID().toString();
            user = new XcUser();
            user.setId(userId);
            user.setWxUnionid(unionid);
            user.setNickname(userInfo.get(WxAuthParam.NICKNAME).toString());
            user.setUserpic(userInfo.get(WxAuthParam.HEADIMGURL).toString());
            user.setName(userInfo.get(WxAuthParam.NICKNAME).toString());
            user.setUsername(unionid);
            user.setPassword(unionid);
            user.setUtype(Dictionary.XcUser.Utype_STUDENT.getCode()); //用户类型
            user.setStatus(Dictionary.XcUser.Status_VALID.getCode()); //用户状态
            int insertUser = xcUserMapper.insert(user);
            if (insertUser < 1) {
                log.error("保存微信用户信息出错");
                XcException.cast("微信认证失败");
            }

            //保存用户角色信息至xc_user_role表
            XcUserRole xcUserRole = new XcUserRole();
            xcUserRole.setId(UUID.randomUUID().toString());
            xcUserRole.setUserId(userId);
            xcUserRole.setRoleId(Dictionary.XcRole.Id_STUDENT.getCode()); //用户角色
            int insertRole = xcUserRoleMapper.insert(xcUserRole);
            if (insertRole < 1) {
                log.error("保存微信用户角色信息出错");
                XcException.cast("微信认证失败");
            }
        }
        //3.2.用户已注册
        user.setUserpic(userInfo.get(WxAuthParam.HEADIMGURL).toString());//更新微信用户头像信息
        xcUserMapper.updateById(user);
        return user;
    }



    /**
     * @description 获取令牌
     * @param code 授权码
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author TMC
     * @date 2023/2/20 13:11
     */
    private Map<String,String> getAccessToken(String code) {

        //1.构建请求url
        String url = String.format(WxAuthParam.ACCESS_TOKEN_URL, appid, secret, code);

        //2.发送请求url
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);

        //3.封装响应
        String result = exchange.getBody();
        return JSON.parseObject(result, Map.class);

    }


    /**
     * @description 携带令牌查询微信用户信息
     * @param access_token 令牌
     * @param openid 授权用户唯一标识
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author TMC
     * @date 2023/2/20 13:30
     */
    private Map<String,Object> getUserInfo(String access_token, String openid) {

        //1.构建请求
        String url = String.format(WxAuthParam.USERINFO_URL, access_token, openid);

        //2.发送请求
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);

        //3.封装响应
        String result = exchange.getBody();
        return JSON.parseObject(result, Map.class);

    }

}
