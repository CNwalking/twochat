package com.cnwalking.twochat.websocket;

import lombok.Data;

import java.io.Serializable;

@Data
public class MsgOfChat implements Serializable {

    private String senderId;
    private String receiverId;
    private String msg;
    private String msgId;    // 为了消息签收

}
