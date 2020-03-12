package com.cnwalking.twochat.dataobject.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;

    private String username;

    private String faceImg;

    private String faceImgBig;

    private String nickname;

    private String qrcode;
}
