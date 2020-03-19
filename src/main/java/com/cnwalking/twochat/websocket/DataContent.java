package com.cnwalking.twochat.websocket;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataContent implements Serializable {
    // 动作类型
    private Integer action;
    private MsgOfChat chatMsg;
    // 扩展字段
    private String extand;

}
