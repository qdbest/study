package com.yucn.service.impl;

import com.yucn.model.UserInfo;
import com.yucn.repository.UserInfoRepository;
import com.yucn.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/11/16.
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Override
    public UserInfo findByUserName(String username) {
        return userInfoRepository.findByUsername(username);
    }
}
