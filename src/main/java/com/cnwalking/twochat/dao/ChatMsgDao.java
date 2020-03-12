package com.cnwalking.twochat.dao;

import com.cnwalking.twochat.dataobject.entity.ChatMsg;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMsgDao {
    int deleteByPrimaryKey(String id);

    int insert(ChatMsg record);

    int insertSelective(ChatMsg record);

    ChatMsg selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ChatMsg record);

    int updateByPrimaryKey(ChatMsg record);
}