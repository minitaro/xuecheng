package com.xuecheng.ucenter.model.dto;

import com.xuecheng.ucenter.model.po.XcUser;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
* @description 用户拓展信息响应dto
* @author TMC
* @date 2023/2/23 0:20
* @version 1.0
*/
@Data
@ToString
public class XcUserExt extends XcUser {

    //用户权限列表
    List<String> permissions = new ArrayList<>();

}
