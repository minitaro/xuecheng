package com.xuecheng.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description 表单工具类
* @author TMC
* @date 2023/2/20 21:55
* @version 1.0
*/
public class FormUtils {

    /**
    * @description 手机号正则校验
    * @param cellphone 手机号
    * @return java.lang.Boolean
    * @author TMC
    * @date 2023/2/24 11:08
    */
    public static Boolean checkCellphone(String cellphone) {

        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cellphone);
        return m.matches();

    }

    /**
    * @description 邮箱正则校验
    * @param email 邮箱
    * @return java.lang.Boolean
    * @author TMC
    * @date 2023/2/24 11:23
    */
    public static Boolean checkEmail(String email) {

        String regex = "^\\w{1,30}@[a-zA-Z0-9]{2,20}(\\.[a-zA-Z0-9]{2,20}){1,2}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.matches();

    }



}
