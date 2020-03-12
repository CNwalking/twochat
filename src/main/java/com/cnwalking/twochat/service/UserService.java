package com.cnwalking.twochat.service;


import com.cnwalking.twochat.dataobject.entity.User;

public interface UserService {
    boolean userNameIsExist(String username);

    boolean checkPswd(String username,String password);

    User register(String username, String password);
}
