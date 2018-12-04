package com.yucn.service;

import com.yucn.model.UserInfo;

/**
 * Created by Administrator on 2018/11/16.
 */
public interface UserInfoService {
    public UserInfo findByUserName(String username);
}
