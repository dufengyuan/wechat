package com.chat.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WSServerInitialzer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // websocket基于http协议,需要http编码解释器
        pipeline.addLast(new HttpServerCodec());
        // 提供对大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        // 对httpmessage进行整合
        pipeline.addLast(new HttpObjectAggregator(1024*64));

        // 添加心跳处理
        pipeline.addLast(new IdleStateHandler(8,10,60));
        pipeline.addLast(new HeartBeatHandler());


        // httpsocket支持
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 添加自定义handler
        pipeline.addLast(new ChatHandler());
    }
}
