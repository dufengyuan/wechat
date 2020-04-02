package com.chat.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
       // 判断evt是否是IdleStateEvent,用于触发用户事件
        if ( evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("进入读空闲");
            } else if (event.state() == IdleState.WRITER_IDLE){
                System.out.println("进入写空闲");
            } else if(event.state() == IdleState.ALL_IDLE) {
                // 读写空闲的handler需要进行关闭
                System.out.println("channel关闭前,连接的用户数量为"+ChatHandler.users.size());
                Channel channel = ctx.channel();
                channel.close();
                System.out.println("关闭当前channel");
            }
        }
    }


}
