package com.cnwalking.twochat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebSocketServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup sloveGroup = new NioEventLoopGroup();
        // 最基础的服务搭建
        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(mainGroup, sloveGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebSocketInit());

            ChannelFuture channelFuture = server.bind(8088).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            sloveGroup.shutdownGracefully();
        }
    }
}
