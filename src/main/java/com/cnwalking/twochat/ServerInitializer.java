package com.cnwalking.twochat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    // channel注册以后,会执行它的初始化方法
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 通过channel去获取对应的pipeline
        ChannelPipeline pipeline = channel.pipeline();
        // pipeline 里面有很多handler,下面的操作添加了一个handler(类似拦截器),是Netty自己提供的一个http编解码api
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        // 再添加一个自定义的助手类
        pipeline.addLast("customHandler", new customHandler());

    }
}
