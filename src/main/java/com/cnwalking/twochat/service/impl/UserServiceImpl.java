package com.cnwalking.twochat.service.impl;

import com.cnwalking.twochat.dao.UserDao;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.service.UserService;
import com.cnwalking.twochat.utils.FastDFSClient;
import com.cnwalking.twochat.utils.FileUtils;
import com.cnwalking.twochat.utils.MD5Encrypt;
import com.cnwalking.twochat.utils.QRCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    FastDFSClient fastDFSClient;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Override
    public boolean userNameIsExist(String username) {
        User user = userDao.selectByUsername(username);
        return ObjectUtils.isEmpty(user) ? false:true;
    }

    @Override
    public User checkPswd(String username,String password) {
        User user = userDao.selectByUsername(username);
        if (!StringUtils.equals(user.getPassword(), MD5Encrypt.md5Encrypt(password))){
            log.info("数据库里的:{},加密完的数据:{}",user.getPassword(),MD5Encrypt.md5Encrypt(password));
            return null;
        }
        return user;
    }

    @Override
    public User register(String username, String password, String cid) {
        User user = new User();
        String userId = Sid.nextShort();
        user.setId(userId);
        user.setFaceImgBig("");
        user.setNickname(username);

        String QRCodePath = "~/Project/imgServer" + userId + "qrcode.png";;
        qrCodeUtils.createQRCode(QRCodePath, "two_chat_qrcode:" + username);
        MultipartFile qrcodeFile = FileUtils.fileToMultipart(QRCodePath);
        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrcodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrCodeUrl);

        user.setUsername(username);
        user.setPassword(MD5Encrypt.md5Encrypt(password));
        user.setFaceImg("");
        user.setCid(cid);
        userDao.insert(user);
        return user;
    }

    @Override
    public void update(User user) {
        userDao.updateByPrimaryKeySelective(user);
    }

    @Override
    public User selectById(String userId) {
        return userDao.selectByPrimaryKey(userId);
    }


}
