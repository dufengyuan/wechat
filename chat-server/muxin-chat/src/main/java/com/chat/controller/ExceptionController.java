package com.chat.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionController {
    @ResponseBody
    @ExceptionHandler({Exception.class})
    private String handleException(Exception e) throws Exception{
        e.printStackTrace();
        return e.getMessage();
    }
}
