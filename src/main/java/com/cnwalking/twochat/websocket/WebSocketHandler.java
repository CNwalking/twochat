package com.cnwalking.twochat.websocket;

import com.alibaba.fastjson.JSON;
import com.cnwalking.twochat.common.MsgActionEnum;
import com.cnwalking.twochat.service.UserService;
import com.cnwalking.twochat.service.impl.UserServiceImpl;
import com.cnwalking.twochat.utils.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于处理文本
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     *   类似于channel的pool
     */
    private static ChannelGroup ClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

//    @Autowired
//    private UserService userService;
//
//    private static WebSocketHandler webSocketHandler;
//
//    @PostConstruct
//    public void init() {
//        webSocketHandler = this;
//    }
    private static UserService userService;
    static {
        userService = SpringUtils.getBean(UserServiceImpl.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 客户端传过来的消息就拿到了
        String content = msg.text();

        Channel currentChannel = ctx.channel();
        System.out.println(content);
        // 转化
        DataContent dataContent = JSON.parseObject(content, DataContent.class);
        System.out.println("转化成功:" + JSON.toJSONString(dataContent));
        Integer action = dataContent.getAction();
        if (action == MsgActionEnum.CONNECT.type) {
            // 初始化channel,把用的channel和userId关联起来
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelMapping.put(senderId, currentChannel);

            // 测试
            for (Channel c : ClientGroup) {
                System.out.println("channelId"+c.id().asLongText());
            }
            UserChannelMapping.output();
            System.out.println("连接结束");
        } else if (action == MsgActionEnum.CHAT.type) {
            System.out.println("chat聊天状态");
            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            MsgOfChat chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();

            // 保存消息到数据库，并且标记为 未签收
            // 无法注入,spring会默认将首字母小写
//            UserService userService = webSocketHandler.userService;
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            // 构造消息
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            Channel receiverChannel = UserChannelMapping.get(receiverId);
            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
            } else {
                // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = ClientGroup.find(receiverChannel.id());
                if (findChannel != null) {
                    // 用户在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContentMsg)));
                } else {
                    // 用户离线 TODO 推送消息
                }
            }

        } else if (action == MsgActionEnum.SIGNED.type) {
            System.out.println("改变读取状态");
            //  2.3  签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
//            UserService userService = webSocketHandler.userService;
            // 扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
            String msgIdsStr = dataContent.getExtand();

            List<String> msgIdList = Arrays.asList(msgIdsStr.split(","));

            System.out.println(msgIdList.toString());

            if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
                // 批量签收
                userService.updateMsgSigned(msgIdList);
            }

        } else if (action == MsgActionEnum.KEEPALIVE.type) {
            //  2.4  心跳类型的消息
            System.out.println("accept from channel [" + currentChannel + "]'s beats...");
        }
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
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常后关闭连接
        ctx.channel().close();
        ClientGroup.remove(ctx.channel());
    }
}
