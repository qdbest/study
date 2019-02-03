package com.yucn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/12/3.
 */
@RestController
@RequestMapping("/hello")
public class Hello {
    @GetMapping
    public String hello(){
        System.out.println("hello");
        return "hello spring security";
    }
}
