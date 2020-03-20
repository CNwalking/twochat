package com.cnwalking.twochat.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

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


        // 如果客户端在1分钟时没有向服务端发送读写心跳(ALL)，则主动断开
        // 如果是读空闲或者写空闲，不处理
        pipeline.addLast(new IdleStateHandler(8, 10, 12));
        // 自定义的空闲状态检测
        pipeline.addLast(new HeartBeatHandler());

        // webSocket服务器处理的协议，用于指定给客户端连接访问的路由
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        pipeline.addLast(new WebSocketHandler());
    }
}
