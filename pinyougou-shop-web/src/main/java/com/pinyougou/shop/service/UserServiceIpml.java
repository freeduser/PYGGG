package com.pinyougou.shop.service;

import com.pinyougou.SellerService;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojo.TbUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 作    者： yingming shen
 * 修改时间： 2019/6/10 19:11.
 * 描   述：  根据输入的用户名查询数据库中的user对象,返回username和password以及认证结果
 * 这里需要连接数据库,要调用其他模块的服务,
 */
public class UserServiceIpml implements UserDetailsService {

    private SellerService sellerService;

    //setter方法是必须的，表明字带为一个属性
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * @param sellerId
     * @return
     * @throws UsernameNotFoundException
     * @descript 根据登陆页面的username【sellerId】，查找数据库中相应卖家信息，对比返回UserDetails对象
     */
    @Override
    public UserDetails loadUserByUsername(String sellerId) throws UsernameNotFoundException {

        //根据登陆页面的username【sellerId】，查找数据库中相应卖家信息
        TbSeller seller = sellerService.selectByPrimaryKey(sellerId);
        if (seller != null) {
            if (seller.getStatus().equals("1")) {   //判断为审核通过
                List<SimpleGrantedAuthority> authrios = new ArrayList<>();
                //相当于赋值操作？基于角色的权限管理;后期这些角色可能会增加，因为不同的角色对应不能的权限
                authrios.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                return new User(sellerId, seller.getPassword(), authrios);
            }

        }
        return  null;
    }
}


