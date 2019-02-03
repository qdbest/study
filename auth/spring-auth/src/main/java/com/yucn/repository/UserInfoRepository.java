package com.yucn.repository;

import com.yucn.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2018/11/16.
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
    public UserInfo findByUsername(String username);
}
