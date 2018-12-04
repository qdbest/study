package com.yucn.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Administrator on 2018/11/16.
 */
@Entity
@Data
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    public enum Role{
        admin,normal
    }
}
