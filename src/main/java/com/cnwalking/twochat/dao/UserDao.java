package com.cnwalking.twochat.dao;

import com.cnwalking.twochat.dataobject.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUsername(String username);
}