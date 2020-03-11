package com.cnwalking.twochat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("UserController")
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    public String test(){
        return "test success";
    }

}
