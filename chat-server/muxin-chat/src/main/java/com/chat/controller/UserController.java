package com.chat.controller;

import com.chat.pojo.ChatMsg;
import com.chat.pojo.FriendRequest;
import com.chat.pojo.MyFriend;
import com.chat.pojo.User;
import com.chat.pojo.Vo.UserVo;
import com.chat.service.UserService;
import com.chat.utils.JSONResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    // 1.注册用户
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public JSONResult insert(@RequestBody User user) {
        // 1.进行非空判断
        if (StringUtils.isEmpty(user.getName())) {
            return JSONResult.error("","用户名不能为空");
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            return JSONResult.error("","密码不能为空");
        }
        // 2.判断用户是否重复
        boolean bool = userService.nameExist(user.getName());
        if (bool) {
            return JSONResult.error("","用户名已存在");
        }
        // 3.进行插入数据库
        user.setNickname(user.getName());

        User result = userService.saveUser(user);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(result,userVo);
        return JSONResult.ok(userVo,"注册成功");
    }

    // 2.获取所有用户
    @RequestMapping(value = "user",method = RequestMethod.GET)
    public List<User> queryUsers() {
        return userService.queryUsers();
    }

    // 3.用户登录功能
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public JSONResult login(@RequestBody User user) {

       User result =  userService.login(user);
       if (result == null) {
           return JSONResult.error(result,"用户名或密码错误,请重新登录");
       } else {
           return JSONResult.ok(result,"登录成功");
       }
    }

    // 4.上传用户头像
    @RequestMapping(value = "uploadFaceImg",method = RequestMethod.POST)
    public JSONResult updateUser(MultipartFile file,String userId) throws Exception {
        User user  = userService.updateUser(file,userId);
        return JSONResult.ok(user,"用户更新上传头像成功");
    }
    // 5.修改用户昵称
    @RequestMapping(value = "updateNickName",method = RequestMethod.PUT)
    public JSONResult updateNickName(@RequestBody User user) {
        userService.updateNickName(user);
        return JSONResult.ok("","更新用户昵称成功");
    }
    // 6. 添加朋友-查询朋友列表
    @RequestMapping(value = "searchFriend",method = RequestMethod.GET)
    public JSONResult searchFriends(String name,String userId) {
        List<User> users = userService.searchFriend(name,userId);
        return JSONResult.ok(users,"查询结果");
    }
    // 7.发送添加朋友请求
    @RequestMapping(value = "friendRequest",method = RequestMethod.POST)
    public JSONResult sendFriendRequest(@RequestBody FriendRequest friendRequest) {
        userService.sendFriendRequest(friendRequest);
        return JSONResult.ok("","发送添加朋友成功");
    }
    // 8.同意添加请求
    @RequestMapping(value = "agreeFriendRequest",method = RequestMethod.POST)
    public JSONResult agreeRequest(@RequestBody FriendRequest friendRequest) {
        userService.passFriendRequest(friendRequest.getSendUserId(),friendRequest.getAcceptUserId());
        return JSONResult.ok("","通过好友请求");
    }
    // 9.获取用户的所有联系人
    public  JSONResult findFriend(String userId) {
        List<MyFriend> friends = userService.queryFriends(userId);
        return JSONResult.ok(friends,"用户联系人");
    }
    // 10.获取用户未读消息
    public JSONResult findUnReadMsg(String userId) {
        List<ChatMsg> msgs = userService.getUnreadMsg(userId);
        return JSONResult.ok(msgs,"获取未读消息");
    }




}
