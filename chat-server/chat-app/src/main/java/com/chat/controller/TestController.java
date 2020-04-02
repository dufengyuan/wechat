package com.chat.controller;

import com.chat.service.UserService;
import com.chat.utils.SpringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {



    @RequestMapping(value = "test",method = RequestMethod.GET)
    public void get() {
        UserService userService = (UserService) SpringUtils.getBean("userService");
        System.out.println(userService);
    }

}
