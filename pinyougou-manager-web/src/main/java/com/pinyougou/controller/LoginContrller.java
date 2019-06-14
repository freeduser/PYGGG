package com.pinyougou.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 作    者： yingming shen
 * 修改时间： 2019/6/11 0:33.
 * 描   述：  根据框架，获得登陆成功的用户名
 */
@RestController
@RequestMapping("/login")
public class LoginContrller {
    @RequestMapping("/name")
    public Map getName(){
        Map<String,String> map = new HashMap<>();
        //得到用户名
        String uname = SecurityContextHolder.getContext().getAuthentication().getName();
        //绑定到Map中并返回
        map.put("name",uname);
        return map;
    }

}
