package com.xuecheng.content.service;

import com.xuecheng.base.validation.constraints.Phone;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.annotation.Validated;

/**
* @description TODO
* @author TMC
* @date 2023/3/3 14:41
* @version 1.0
*/
@SpringBootTest
@Validated
public class PhoneRegexpTest {

    @Phone
    private String phone = "122";

    @Test
    public void phoneTest() {

        System.out.println(phone);

    }
}
