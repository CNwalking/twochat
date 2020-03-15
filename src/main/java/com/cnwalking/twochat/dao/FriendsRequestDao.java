package com.cnwalking.twochat.dao;

import com.cnwalking.twochat.dataobject.dto.FriendRequestDto;
import com.cnwalking.twochat.dataobject.entity.FriendsRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendsRequestDao {
    int deleteByPrimaryKey(String id);

    int insert(FriendsRequest record);

    int insertSelective(FriendsRequest record);

    FriendsRequest selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FriendsRequest record);

    int updateByPrimaryKey(FriendsRequest record);

    FriendsRequest selectBySendId(@Param("sendUserId") String sendUserId, @Param("acceptUserId") String acceptUserId);

    List<FriendRequestDto> sendList(String acceptUserId);
}