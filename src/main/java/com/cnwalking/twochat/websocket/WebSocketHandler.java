package com.cnwalking.twochat.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

/**
 * 用于处理文本
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     *   类似于channel的pool
     */
    private static ChannelGroup ClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 客户端传过来的消息就拿到了
        String content = msg.text();
        System.out.println("客户端的消息:"+content);
        // ==== 方法1 ====
        ClientGroup.forEach(ele->{
            ele.writeAndFlush(new TextWebSocketFrame("[method 1]Time now " + LocalDateTime.now() + " msg is " + content));
        });
        // ==== 方法2 ====
//        ClientGroup.writeAndFlush(new TextWebSocketFrame("[method 2]Time now " + LocalDateTime.now() + " msg is " + content));

    }

    /**
     * 客户端接上以后，塞到pool里面去
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ClientGroup.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 其实不写也直接移除了
        ClientGroup.remove(ctx.channel());
        System.out.println("长id:"+ctx.channel().id().asLongText());
        System.out.println("短id:"+ctx.channel().id().asShortText());

    }
}
