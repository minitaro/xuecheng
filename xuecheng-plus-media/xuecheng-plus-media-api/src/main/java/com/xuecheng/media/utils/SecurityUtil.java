package com.xuecheng.media.utils;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XcException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* @description 获取登录用户对象
* @author TMC
* @date 2023/2/19 17:34
* @version 1.0
*/
@Slf4j
public class SecurityUtil {

    public static XcUser getUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        XcUser xcUser = null;
        if (principal instanceof String){
            String jsonString = (String) principal;
            try {
                xcUser = JSON.parseObject(jsonString, XcUser.class);
            } catch (Exception e) {
                log.debug("解析jwt中的用户身份无法转成XcUser对象:{}", jsonString);
            }
        }
        if (xcUser == null) {
            XcException.cast("用户未登录");
        }
        return xcUser;

    }

    @Data
    public static class XcUser implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id;

        private String username;

        private String password;

        private String salt;

        private String name;
        private String nickname;
        private String wxUnionid;
        private String companyId;
        /**
         * 头像
         */
        private String userpic;

        private String utype;

        private LocalDateTime birthday;

        private String sex;

        private String email;

        private String cellphone;

        private String qq;

        /**
         * 用户状态
         */
        private String status;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;


    }
}
