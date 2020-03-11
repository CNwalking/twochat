package com.cnwalking.twochat.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        // webSocket基于http协议,所以需要http编解码器
        pipeline.addLast(new HttpServerCodec());
        // 对大数据流的支持(分块操作)
        pipeline.addLast(new ChunkedWriteHandler());
        // 聚合操作，聚合成FullHttpRequest或FullHttpResponse。设定消息最大长度
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        // =======  以上三个在Netty变成中几乎都会用到  ========

        // webSocket服务器处理的协议，用于指定给客户端连接访问的路由
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        pipeline.addLast(new WebSocketHandler());
    }
}
