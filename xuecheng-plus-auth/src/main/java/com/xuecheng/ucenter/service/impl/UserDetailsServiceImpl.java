package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description 用户详情服务实现类
* @author TMC
* @date 2023/2/19 16:30
* @version 1.0
*/
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcMenuMapper xcMenuMapper;

    @Autowired
    ApplicationContext applicationContext;


    /**
    * @description 查询用户详情
    * @param username json格式的认证请求dto
    * @return org.springframework.security.core.userdetails.UserDetails
    * @author TMC
    * @date 2023/2/19 16:32
    */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //1.认证请求dto类型转换:Json-->AuthParamsDto
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(username, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}", username);
            throw new RuntimeException("认证请求数据格式错误");
        }

        //2.根据指定认证方式进行认证
        String authType = authParamsDto.getAuthType();
        AuthService authService = applicationContext.getBean(authType + "_authservice", AuthService.class);
        XcUserExt user = authService.authenticate(authParamsDto);

        //3.将认证成功的用户拓展信息封装为用户详情返回
        return this.toUserDetails(user);

    }

    /**
    * @description 封装用户详情
    * @param user 用户拓展信息
    * @return org.springframework.security.core.userdetails.UserDetails
    * @author TMC
    * @date 2023/2/19 21:01
    */
    private UserDetails toUserDetails(XcUserExt user) {

        //1.查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());
        List<String> permissions = new ArrayList<>();
        if (xcMenus == null || xcMenus.size() == 0) {
            //没有权限则添加默认值p1,权限列表不能为空,否则报错"Cannot pass a null GrantedAuthority collection"
            permissions.add("p1");
        }else {
            xcMenus.forEach(menu -> permissions.add(menu.getCode()));
        }
        user.setPermissions(permissions);

        //2.用户密码
        String password = user.getPassword();

        //3.用户信息拓展
        //3.1.为了安全,令牌中的用户信息不包含密码
        user.setPassword(null);
        //3.2.将user对象转json
        String userString = JSON.toJSONString(user);

        //4.封装UserDetails对象返回
        UserDetails userDetails = User.withUsername(userString)
                                      .password(password)
                                      .authorities(permissions.toArray(new String[0]))
                                      .build();
        return userDetails;

    }
}
