package com.chat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class WSServer {
    // 1.创建ws服务器实例
    private static class SingleInstance  {
       static  final  WSServer instance = new WSServer();
    }
    // 2.获取服务器实例
    public static WSServer getInstance() {
        return SingleInstance.instance;
    }

    private EventLoopGroup mainGroup; // 主线程
    private EventLoopGroup subGroup;  // 从线程
    private ServerBootstrap server;   // 服务器
    private ChannelFuture future;    // 连接实例

    public WSServer() {
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerInitialzer()); //暂无设置
    }

    // 启动服务器
    public void start() {
        this.future = server.bind(9001);
        System.out.println("netty服务器开始启动");
    }
}
