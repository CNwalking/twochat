package com.cnwalking.twochat.service.impl;

import com.cnwalking.twochat.dao.UserDao;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.service.UserService;
import com.cnwalking.twochat.utils.MD5Encrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean userNameIsExist(String username) {
        User user = userDao.selectByUsername(username);
        return ObjectUtils.isEmpty(user) ? false:true;
    }

    @Override
    public boolean checkPswd(String username,String password) {
        User user = userDao.selectByUsername(username);
        if (!StringUtils.equals(user.getPassword(), MD5Encrypt.md5Encrypt(password))){
            log.info("数据库里的:{},加密完的数据:{}",user.getPassword(),MD5Encrypt.md5Encrypt(password));
            return false;
        }
        return true;
    }

    @Override
    public User register(String username, String password) {
        User user = new User();
        String userId = Sid.nextShort();
        user.setId(userId);
        user.setFaceImgBig("");
        user.setNickname("");
        user.setQrcode("");
        user.setUsername(username);
        user.setPassword(MD5Encrypt.md5Encrypt(password));
        user.setFaceImg("");
        userDao.insert(user);
        return user;
    }


}
