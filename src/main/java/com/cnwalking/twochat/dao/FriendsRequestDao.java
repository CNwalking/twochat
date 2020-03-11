package com.cnwalking.twochat.dao;

import com.cnwalking.twochat.dataobject.entity.FriendsRequest;

public interface FriendsRequestDao {
    int deleteByPrimaryKey(String id);

    int insert(FriendsRequest record);

    int insertSelective(FriendsRequest record);

    FriendsRequest selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FriendsRequest record);

    int updateByPrimaryKey(FriendsRequest record);
}