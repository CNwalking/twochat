package com.cnwalking.twochat.service;


import com.cnwalking.twochat.dataobject.dto.FriendRequestDto;
import com.cnwalking.twochat.dataobject.entity.User;

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
}
