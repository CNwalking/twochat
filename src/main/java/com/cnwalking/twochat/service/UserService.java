package com.cnwalking.twochat.service;


import com.cnwalking.twochat.dataobject.dto.FriendRequestDto;
import com.cnwalking.twochat.dataobject.dto.FriendsListDto;
import com.cnwalking.twochat.dataobject.dto.UserDto;
import com.cnwalking.twochat.dataobject.entity.ChatMsg;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.websocket.MsgOfChat;

import java.util.List;

public interface UserService {
    boolean userNameIsExist(String username);

    User checkPswd(String username,String password);

    User register(String username, String password, String cid);

    void update(User user);

    User selectById(String userId);

    User selectByUsername(String username);

    String searchUserListByName(String userId ,String username);

    String sendAddFriendsRequest(String userId, String friendId);

    List<FriendRequestDto> sendList(String acceptUserId);

    void deleteAddFriendsReq(String sendUserId,String acceptUserId);

    void insertIntoMapping(String sendUserId, String acceptUserId);

    List<FriendsListDto> getFriendsList(String userId);

    String saveMsg(MsgOfChat chatMsg);

    void updateMsgSigned(List<String> msgIdList);

    List<ChatMsg> getUnReadMsgList(String acceptUserId);

}
