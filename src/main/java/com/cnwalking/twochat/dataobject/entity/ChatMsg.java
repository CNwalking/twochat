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

    private String msg;

    private Integer signFlag;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}