package com.cnwalking.twochat.dataobject.dto;

import lombok.Data;

@Data
public class FriendRequestDto {
    private String sendUserId;
    private String sendUsername;
    private String sendFaceImg;
    private String sendNickname;
}
