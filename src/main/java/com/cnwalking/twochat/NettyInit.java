package com.cnwalking.twochat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyInit {
    public static void main(String[] args) throws Exception {
        // 定义线程组,主接受客户端连接不作处理；从来做分配给channel做任务
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup slaveGroup = new NioEventLoopGroup();

        try {
            // 不是前段那个css框架!它负责初始化netty服务器，并且开始监听端口的socket请求
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 设置主从group
            serverBootstrap.group(mainGroup, slaveGroup)
                    // 设置通道类型
                    .channel(NioServerSocketChannel.class)
                    // Netty的子处理器,用来处理slaveGroup
                    .childHandler(new ServerInitializer());

            // 绑定端口，设置同步方式
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();
            // 监听关闭的channel，设置同步方式
            channelFuture.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            slaveGroup.shutdownGracefully();
        }


    }
}
