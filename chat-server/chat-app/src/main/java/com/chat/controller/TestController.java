package com.chat.controller;

import com.chat.service.UserService;
import com.chat.utils.SpringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;

@RestController
public class TestController {



    @RequestMapping(value = "test",method = RequestMethod.GET)
    public void get() throws FileNotFoundException {
        UserService userService = (UserService) SpringUtils.getBean("userService");
        System.out.println(userService);
        File path = new File(ResourceUtils.getURL("TestController").getPath());

        System.out.println("path:"+path.getAbsolutePath());

//如果上传目录为/static/images/upload/，则可以如下获取：
        File upload = new File(path.getAbsolutePath(),"static/images/upload/");


    }

}
