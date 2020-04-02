package com.chat.netty;



import io.netty.channel.Channel;

import java.util.HashMap;

public class UserChannelRel {
    // 进行声明连接管理者
    private static HashMap<String, Channel> manager = new HashMap<>();
    // 添加连接者
    public static  void put(String userId, Channel channel){
        manager.put(userId,channel);
    }
    // 获取连接者
    public static Channel get(String userId) {
        return manager.get(userId);
    }
    // 删除连接者
    public static  void output(String userId) {
        manager.remove(userId);
    }
    public static HashMap getManage() {
        return manager;
    }
}
