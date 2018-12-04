package com.yucn.init;

import com.yucn.model.UserInfo;
import com.yucn.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by Administrator on 2018/11/16.
 */
@Service
public class DataInit {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void dataInit(){
        UserInfo admin=new UserInfo();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setRole(UserInfo.Role.admin);
        userInfoRepository.save(admin);

        UserInfo user=new UserInfo();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123"));
        user.setRole(UserInfo.Role.normal);
        userInfoRepository.save(user);

    }


}
