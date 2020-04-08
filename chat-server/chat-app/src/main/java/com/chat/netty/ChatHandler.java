package com.chat.netty;

import com.alibaba.fastjson.JSON;
import com.chat.pojo.ChatMsg;
import com.chat.service.UserService;
import com.chat.utils.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;



import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // 1. 用于记录和管理所有用户的记录
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        // 1. 每一个用户连接都会分配一个唯一编号的chenanel获取当前channel
        Channel channel = channelHandlerContext.channel();
        // 2. 获取客户端传送过来的信息
        DataContent dataContent = JSON.parseObject(textWebSocketFrame.text(),DataContent.class);
       //  3. 获取消息类型
        Integer action = dataContent.getAction();
        if (action == 0) {
            // 第一次进行连接时,初始化channel,将channel和userId进行关联
            String userId = dataContent.getChatMsg().getSendUserId();
            UserChannelRel.put(userId,channel);
            HashMap map = UserChannelRel.getManage();
        } else if (action == 1) {
            // 1. 如果是聊天的类型,将聊天记录保存到数据库中
            ChatMsg chatMsg = dataContent.getChatMsg();
            UserService userService = (UserService) SpringUtils.getBean("userService");
            ChatMsg result = userService.saveMsg(chatMsg);
            // 2.将消息记录推送给接受者
            dataContent.setChatMsg(result);
            Channel receiverChannel = UserChannelRel.get(chatMsg.getAcceptUserId());
            if (receiverChannel == null) {
                // 表示接受者离线，通过第三方进行推送消息
            } else  {
                // 需要从channelGroup中进行判断
                Channel findChannel = users.find(receiverChannel.id());
                HashMap map = UserChannelRel.getManage();
                dataContent.setExtend(JSON.toJSONString(users));
                if (findChannel != null) {
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent)));
                } else {
                    // 用户不在线
                }
            }

        } else if (action == 2) {
            UserService userService = (UserService) SpringUtils.getBean("userService");
            // 签收消息类型 将数据库中的消息签收状态进行更改
            String expand = dataContent.getExtend();
            String[] msgIds = expand.split(",");
            List<String> ids = new ArrayList();
            for(String id: msgIds) {
                // 添加id非空判断
                ids.add(id);
                userService.reagMsg(id);
            }
        } else if (action == 3) {
            // 心跳类型的消息
            System.out.println("心跳类型消息");
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       // 打印异常信息
        cause.printStackTrace();
        // 关闭连接并删除连接
        ctx.channel().close();
        users.remove(ctx.channel());

    }
}
