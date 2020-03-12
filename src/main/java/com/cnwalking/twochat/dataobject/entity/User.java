package com.cnwalking.twochat.dataobject.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * user
 * @author 
 */
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 小头像
     */
    private String faceImg;

    /**
     * 大头像
     */
    private String faceImgBig;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 二维码
     */
    private String qrcode;

    /**
     * 设备id
     */
    private String cid;

    private static final long serialVersionUID = 1L;
}