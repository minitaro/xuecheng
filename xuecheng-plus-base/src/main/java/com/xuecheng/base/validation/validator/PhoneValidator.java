package com.xuecheng.base.validation.validator;

import com.xuecheng.base.validation.constraints.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
* @description 手机号码正则校验
* @author TMC
* @date 2023/3/3 12:40
* @version 1.0
*/
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern PHONE_REGEX = Pattern.compile("^1(3|4|5|7|8)\\d{9}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if ( value == null || value.length() == 0 ) {
            return true;
        }
        return PHONE_REGEX.matcher(value).matches();
    }
}
