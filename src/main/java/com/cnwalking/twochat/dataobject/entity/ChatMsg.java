package com.cnwalking.twochat.dataobject.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * chat_msg
 * @author 
 */
@Data
public class ChatMsg implements Serializable {
    /**
     * 用户id
     */
    private String id;

    /**
     * 发送方id
     */
    private String sendUserId;

    /**
     * 接受方id
     */
    private String acceptUserId;

    /**
     * 小头像
     */
    private String msg;

    /**
     * 大头像
     */
    private Integer signFlag;

    /**
     * 昵称
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}