package com.cnwalking.twochat.dao;

import com.cnwalking.twochat.dataobject.entity.Mapping;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MappingDao {
    int deleteByPrimaryKey(String id);

    int insert(Mapping record);

    int insertSelective(Mapping record);

    Mapping selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Mapping record);

    int updateByPrimaryKey(Mapping record);

    List<Mapping> selectByMyUserId(String myUserId);

}