package com.yucn.service;

import com.yucn.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/16.
 */
@Component
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserInfo userInfo=userInfoService.findByUserName(s);
        if(userInfo==null){
            throw new UsernameNotFoundException("not found");
        }
        List<GrantedAuthority> authorities =new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+userInfo.getRole().name()));
        User user=new User(userInfo.getUsername(),userInfo.getPassword(),authorities);
        return user;
    }
}
