package com.cnwalking.twochat.dataobject.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * friends_request
 * @author 
 */
@Data
public class FriendsRequest implements Serializable {
    /**
     * 请求的id
     */
    private String id;

    /**
     * 发送的用户id
     */
    private String sendUserId;

    /**
     * 接受的用户id
     */
    private String acceptUserId;

    /**
     * 发送时间
     */
    private Date requestDateTime;

    private static final long serialVersionUID = 1L;
}