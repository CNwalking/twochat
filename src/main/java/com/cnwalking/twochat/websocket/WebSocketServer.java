package com.cnwalking.twochat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketServer {

    private EventLoopGroup mainGroup;
    private EventLoopGroup slaveGroup;
    private ServerBootstrap server;
    private ChannelFuture channelFuture;

    // 单例模式
    private static class SingletionWSServer{
        static final WebSocketServer instance = new WebSocketServer();
    }

    public static WebSocketServer getInstance() {
        return SingletionWSServer.instance;
    }

    public WebSocketServer(){
        mainGroup = new NioEventLoopGroup();
        slaveGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(mainGroup, slaveGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebSocketInit());
    }

    public void start(){
        this.channelFuture = server.bind(8088);
        log.info("Netty WebSocket server start");
    }


}
