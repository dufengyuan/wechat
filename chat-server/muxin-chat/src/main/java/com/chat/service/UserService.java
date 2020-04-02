package com.chat.service;

import com.chat.mapper.ChatMsgMapper;
import com.chat.mapper.FriendRequestMapper;
import com.chat.mapper.MyFriendMapper;
import com.chat.mapper.UserMapper;
import com.chat.netty.UserChannelRel;
import com.chat.pojo.*;
import com.chat.utils.IdWorker;

import com.chat.utils.QrCodeUtil;
import com.chat.utils.SearchFriendStatus;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService  {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private QrCodeUtil qrCodeUtil;

    @Autowired
    private MyFriendMapper myFriendMapper;

    @Autowired
    private FriendRequestMapper friendRequestMapper;

    @Autowired
    private ChatMsgMapper chatMsgMapper;


    // 1.查询所有用户信息
    public List<User> queryUsers() {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        Page page = PageHelper.startPage(1,2);
        List<User> users = userMapper.selectByExample(userExample);
        return users;
    }
    // 2.根据名字查询用户是否存在
    public boolean nameExist(String name) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo(name);
        List<User> user = userMapper.selectByExample(userExample);
        if (user.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    // 3.注册用户
    public User saveUser(User user) {
        user.setId(idWorker.nextId()+"");
        // 生成二维码
        // 1.设置图片路径
        String qrCodePath = "D:\\example\\chatnetty\\muxin-chat\\src\\main\\resources\\static\\images\\"+user.getName()+"qrcode.png";
        // 2.生成二维码图片
        qrCodeUtil.createQRCode(qrCodePath,"chatCode:"+user.getName());
        // 3.将图片转为file文件类型
        user.setQrcode(user.getName()+"qrcode.png");
        userMapper.insert(user);
        return user;
    }
    // 4.用户登录
    public User login(User user) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo(user.getName());
        criteria.andPasswordEqualTo(user.getPassword());
        List<User> result = userMapper.selectByExample(userExample);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }
    // 5. 用户上传头像
    public User updateUser(MultipartFile file,String userId) throws Exception {
        User user = userMapper.selectByPrimaryKey(userId);
        String path = "D:\\example\\chatnetty\\muxin-chat\\src\\main\\resources\\static\\images\\"+file.getOriginalFilename();
        File input = new File(path);
        file.transferTo(input);
        user.setFaceImg(file.getOriginalFilename());
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }
    // 6.更新用户昵称
    public User updateNickName(User user) {
        User result = userMapper.selectByPrimaryKey(user.getId());
        result.setNickname(user.getNickname());
        userMapper.updateByPrimaryKeySelective(result);
        return result;
    }
    // 7.根据用户名模糊查询所有用户信息
    public List<User> queryByName(String name) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameLike("%"+name+"%");
        List<User> result = userMapper.selectByExample(userExample);
        return result;
    }
    // 8.查询用户
    public List<User> searchFriend(String name,String userId) {
        List<User> users = queryByName(name);
        // 1.如果是自己将自己进行过滤
        for(int i = 0; i < users.size(); i++) {
            String id = users.get(i).getId();
            if ( id.equals(userId) ) {
                System.out.println("过滤用户自己");
                users.remove(i);
            }
        }
        // 2.过滤已是好友的朋友
        MyFriendExample myFriendExample = new MyFriendExample();
        MyFriendExample.Criteria criteria = myFriendExample.createCriteria();
        criteria.andMyUserIdEqualTo(userId);
        List<MyFriend> friends = myFriendMapper.selectByExample(myFriendExample);
        Map<String,String> map = new HashMap();
        for (int i = 0; i< friends.size();i++) {
            map.put(friends.get(i).getMyFriendId(),friends.get(i).getMyUserId());
        }
        for(int i = 0; i<users.size();i++) {
            String id = users.get(i).getId();
            if (map.get(id) != null) {
                users.remove(i);
            }
        }
        // 3.如果查询不到用户
        return users;
    }
    // 9.发送添加朋友申请
    public FriendRequest  sendFriendRequest(FriendRequest friendRequest) {
        FriendRequestExample friendRequestExample = new FriendRequestExample();
        FriendRequestExample.Criteria criteria = friendRequestExample.createCriteria();
        criteria.andAcceptUserIdEqualTo(friendRequest.getAcceptUserId());
        criteria.andSendUserIdEqualTo(friendRequest.getSendUserId());
        List<FriendRequest> result = friendRequestMapper.selectByExample(friendRequestExample);
        // 进行判断请求是否发送
        if (result.size() > 0) {
            return null;
        }
        friendRequest.setId(idWorker.nextId()+"");
        friendRequest.setCreatetime(new Date());
        friendRequestMapper.insert(friendRequest);
        // 服务端通知接收者更新联系人信息-添加提示
        io.netty.channel.Channel channel =  UserChannelRel.get(friendRequest.getAcceptUserId());
        channel.writeAndFlush(new TextWebSocketFrame("给接受者发送提示信息"));
        return   friendRequest;
    }

    //  朋友接收同意请求-发送请求之后需要立即进行通知好友进行接收请求信息
    public List<FriendRequest> queryRequest(String userId) {
        FriendRequestExample friendRequestExample = new FriendRequestExample();
        FriendRequestExample.Criteria criteria = friendRequestExample.createCriteria();
        criteria.andAcceptUserIdEqualTo(userId);
        return friendRequestMapper.selectByExample(friendRequestExample);
    }
    // 进行删除请求
    public void deleteFriendRequest(String sendUserId,String acceptUserId) {
        FriendRequestExample friendRequest = new FriendRequestExample();
        FriendRequestExample.Criteria criteria = friendRequest.createCriteria();
        criteria.andAcceptUserIdEqualTo(acceptUserId);
        criteria.andSendUserIdEqualTo(sendUserId);
        friendRequestMapper.deleteByExample(friendRequest);
    }
    // 保存朋友
    public void saveFriend(String sendUserId,String acceptUserId ) {
        MyFriend myFriend =new MyFriend();
        myFriend.setId(idWorker.nextId()+"");
        myFriend.setMyUserId(sendUserId);
        myFriend.setMyFriendId(acceptUserId);
        myFriendMapper.insert(myFriend);

    }
    // 10.通过朋友请求
    @Transactional
    public void passFriendRequest(String userId,String friendId) {
        // 相互保存记录
        saveFriend(userId,friendId);
        saveFriend(friendId,userId);
        // 删除请求记录
        deleteFriendRequest(userId,friendId);
        // 服务端主动通知请求发起者更新联系人
        Channel channel = UserChannelRel.get(friendId);
        channel.writeAndFlush(new TextWebSocketFrame("朋友更新好友列表"));
    }


    // 获取用户的朋友列表
    public List<MyFriend> queryFriends(String userId) {
        MyFriendExample myFriendExample = new MyFriendExample();
        MyFriendExample.Criteria criteria = myFriendExample.createCriteria();
        criteria.andMyUserIdEqualTo(userId);
        return  myFriendMapper.selectByExample(myFriendExample);
    }
    // 添加聊天记录
    public ChatMsg saveMsg(ChatMsg msg) {
        msg.setId(idWorker.nextId()+"");
        msg.setSignFlag(0);
        msg.setCreatetime(new Date());
        chatMsgMapper.insert(msg);
        return msg;
    }
    // 读取消息
    public void reagMsg(String id) {
        ChatMsg msg = chatMsgMapper.selectByPrimaryKey(id);
        msg.setSignFlag(1);
       chatMsgMapper.updateByPrimaryKeySelective(msg);
    }
    // 获取未读信息
    public List<ChatMsg> getUnreadMsg(String userId) {
        ChatMsgExample chatMsgExample = new ChatMsgExample();
        ChatMsgExample.Criteria criteria = chatMsgExample.createCriteria();
        criteria.andSendUserIdEqualTo(userId);
        return chatMsgMapper.selectByExample(chatMsgExample);
    }









}
