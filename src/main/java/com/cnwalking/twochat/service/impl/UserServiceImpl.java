package com.cnwalking.twochat.service.impl;

import com.cnwalking.twochat.dao.ChatMsgDao;
import com.cnwalking.twochat.dao.FriendsRequestDao;
import com.cnwalking.twochat.dao.MappingDao;
import com.cnwalking.twochat.dao.UserDao;
import com.cnwalking.twochat.dataobject.dto.FriendRequestDto;
import com.cnwalking.twochat.dataobject.dto.FriendsListDto;
import com.cnwalking.twochat.dataobject.entity.ChatMsg;
import com.cnwalking.twochat.dataobject.entity.FriendsRequest;
import com.cnwalking.twochat.dataobject.entity.Mapping;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.service.UserService;
import com.cnwalking.twochat.utils.FastDFSClient;
import com.cnwalking.twochat.utils.FileUtils;
import com.cnwalking.twochat.utils.MD5Encrypt;
import com.cnwalking.twochat.utils.QRCodeUtils;
import com.cnwalking.twochat.websocket.MsgOfChat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    FastDFSClient fastDFSClient;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MappingDao mappingDao;

    @Autowired
    private FriendsRequestDao friendsRequestDao;

    @Autowired
    private ChatMsgDao chatMsgDao;

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

    @Override
    public User selectByUsername(String username) {
        return userDao.selectByUsername(username);
    }

    @Override
    public String searchUserListByName(String userId, String username) {
        String resultMsg = "";
        // 情况1 搜索的用户不存在
        User serchTarget = userDao.selectByUsername(username);
        if (ObjectUtils.isEmpty(serchTarget)) {
            return "User does not exist";
        }
        String targetUserId = serchTarget.getId();
        // 情况2 搜索的用户是自己
        if (targetUserId.equals(userId)) {
            return "You can not add yourself";
        }
        // 情况3 搜索的用户已经是好友了
        List<Mapping> mappingList = mappingDao.selectByMyUserId(userId);
        if (!CollectionUtils.isEmpty(mappingList)) {
            for (int i = 0; i < mappingList.size(); i++) {
                if (mappingList.get(i).getFriendUserId().equals(targetUserId)) {
                    return "He is already your friends";
                }
            }
        }
        return resultMsg;
    }

    @Override
    public String sendAddFriendsRequest(String userId, String friendId) {
        // 不能重复提交
        FriendsRequest request = friendsRequestDao.selectBySendId(userId,friendId);
        if (!ObjectUtils.isEmpty(request)) {
            return "Already Send Add Friends Request";
        }
        FriendsRequest insertRequest = new FriendsRequest();
        String reqId = Sid.nextShort();
        insertRequest.setId(reqId);
        insertRequest.setSendUserId(userId);
        insertRequest.setAcceptUserId(friendId);
        insertRequest.setRequestDateTime(new Date());
        friendsRequestDao.insert(insertRequest);
        return "Send success";
    }

    @Override
    public List<FriendRequestDto> sendList(String acceptUserId) {
        return friendsRequestDao.sendList(acceptUserId);
    }

    @Override
    public void deleteAddFriendsReq(String sendUserId,String acceptUserId) {
        FriendsRequest request = friendsRequestDao.selectBySendId(sendUserId,acceptUserId);
        if (!ObjectUtils.isEmpty(request)) {
            friendsRequestDao.deleteByPrimaryKey(request.getId());
        }
    }

    @Override
    public void insertIntoMapping(String sendUserId, String acceptUserId) {
        Mapping mapping1= new Mapping();
        String id1 = Sid.nextShort();
        mapping1.setId(id1);
        mapping1.setMyUserId(sendUserId);
        mapping1.setFriendUserId(acceptUserId);

        Mapping mapping2= new Mapping();
        String id2 = Sid.nextShort();
        mapping2.setId(id2);
        mapping2.setMyUserId(acceptUserId);
        mapping2.setFriendUserId(sendUserId);

        mappingDao.insert(mapping1);
        mappingDao.insert(mapping2);


    }

    @Override
    public List<FriendsListDto> getFriendsList(String userId) {
        List<FriendsListDto> list = mappingDao.selectFriendListByMyUserId(userId);
        return list;
    }

    @Override
    public String saveMsg(MsgOfChat chatMsg) {
        ChatMsg msg = new ChatMsg();
        String msgId = Sid.nextShort();
        msg.setId(msgId);
        msg.setSendUserId(chatMsg.getSenderId());
        msg.setAcceptUserId(chatMsg.getReceiverId());
        msg.setMsg(chatMsg.getMsg());
        // 0 未签收 1签收
        msg.setSignFlag(0);
        msg.setCreateTime(new Date());

        chatMsgDao.insert(msg);
        return msgId;
    }

    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        chatMsgDao.updateByMsgIdList(msgIdList);
    }

    @Override
    public List<ChatMsg> getUnReadMsgList(String acceptUserId) { return chatMsgDao.getUnReadMsgList(acceptUserId); }
}
