package com.cnwalking.twochat.controller;

import com.alibaba.fastjson.JSON;
import com.cnwalking.twochat.common.MsgActionEnum;
import com.cnwalking.twochat.common.Response;
import com.cnwalking.twochat.dataobject.dto.FriendRequestDto;
import com.cnwalking.twochat.dataobject.dto.FriendsListDto;
import com.cnwalking.twochat.dataobject.dto.UserDto;
import com.cnwalking.twochat.dataobject.dto.UserVo;
import com.cnwalking.twochat.dataobject.entity.ChatMsg;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.service.UserService;
import com.cnwalking.twochat.utils.FastDFSClient;
import com.cnwalking.twochat.utils.FileUtils;
import com.cnwalking.twochat.utils.ResponseUtils;
import com.cnwalking.twochat.websocket.DataContent;
import com.cnwalking.twochat.websocket.UserChannelMapping;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Api(tags = "UserController", description = "用户模块")
@RestController("UserController")
@RequestMapping("/user")
public class UserController {

    @Autowired
    FastDFSClient fastDFSClient;

    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String test(){
        return "test success";
    }

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping(value = "/loginOrRegister")
    public Response userLogin(
            @ApiParam(name = "username", value = "用户名") @RequestParam(value = "username") String username,
            @ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password,
            @ApiParam(name = "cid", value = "设备号") @RequestParam(value = "cid") String cid) {
        log.info("用户登录, username:{}, password:{}, cid:{}", username, password, cid);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ResponseUtils.returnDefaultError();
        }
        if (userService.userNameIsExist(username)) {
            // 存在 就登录
            User user = userService.checkPswd(username, password);
            if (ObjectUtils.isEmpty(user)) {
                return ResponseUtils.returnDefaultError();
            }else {
                UserDto dto = new UserDto();
                BeanUtils.copyProperties(user,dto);
                return ResponseUtils.returnSuccess(dto);
            }
        }else {
            // 不存在 就注册
            User user = userService.register(username, password, cid);
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(user,dto);
            return ResponseUtils.returnSuccess(dto);
        }
    }

    @PostMapping(value = "/uploadImg")
    public Response uploadImg( @RequestBody UserVo vo) throws Exception{
        log.info("图片上传, userId:{}, base64:{}", vo.getUserId(), vo.getBase64());
        String userFacePath = "~/Project/imgServer" + vo.getUserId() + "_faceImg.png";
        if (StringUtils.isBlank(vo.getUserId()) || StringUtils.isBlank(vo.getBase64())) {
            return ResponseUtils.returnDefaultError();
        }
        // 传文件
        FileUtils.base64ToFile(userFacePath, vo.getBase64());
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);
        // 缩略图url拼接
        String join = "_80x80.";
        String[] a = url.split("\\.");
        // 小图路径
        String smallUrl = a[0] + join + a[1];

        // 更新用户头像
        User user = new User();
        user.setFaceImg(smallUrl);
        user.setFaceImgBig(url);
        user.setId(vo.getUserId());
        userService.update(user);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(userService.selectById(vo.getUserId()),dto);
        return ResponseUtils.returnSuccess(dto);
    }

    @ApiOperation(value = "设置昵称", notes = "设置昵称")
    @PostMapping(value = "/setNickname")
    public Response setNickname(
            @ApiParam(name = "userId", value = "用户id") @RequestParam(value = "userId") String userId,
            @ApiParam(name = "nickName", value = "用户昵称") @RequestParam(value = "nickName") String nickName
    ) throws Exception{
        log.info("设置昵称, userId:{},nickName:{}", userId,nickName);
        if (StringUtils.isBlank(nickName)|| StringUtils.isBlank(userId)) {
            return ResponseUtils.returnDefaultError();
        }
        // 更新用户头像
        User user = new User();
        user.setNickname(nickName);
        user.setId(userId);
        userService.update(user);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(userService.selectById(userId),dto);
        return ResponseUtils.returnSuccess(dto);
    }

    @ApiOperation(value = "搜索好友", notes = "搜索好友")
    @PostMapping(value = "/searchFriends")
    public Response searchFriends(
            @ApiParam(name = "userId", value = "本人的用户id") @RequestParam(value = "userId") String userId,
            @ApiParam(name = "username", value = "搜索目标的用户名") @RequestParam(value = "username") String username
    ) throws Exception{
        log.info("搜索好友, userId:{},username:{}", userId,username);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(userId)) {
            return ResponseUtils.returnDefaultError();
        }
        String msg = userService.searchUserListByName(userId,username);
        if (!msg.equals("")) {
            return ResponseUtils.returnError(400,msg);
        }
        User targetUser = userService.selectByUsername(username);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(targetUser,dto);
        return ResponseUtils.returnSuccess(dto);
    }

    @ApiOperation(value = "发送添加好友请求", notes = "发送添加好友请求")
    @PostMapping(value = "/addFriends")
    public Response addFriends(
            @ApiParam(name = "userId", value = "本人的用户id") @RequestParam(value = "userId") String userId,
            @ApiParam(name = "friendId", value = "搜索目标的用户id") @RequestParam(value = "friendId") String friendId
    ) throws Exception{
        log.info("发送添加好友请求, userId:{},friendId:{}", userId,friendId);
        if (StringUtils.isBlank(friendId) || StringUtils.isBlank(userId)) {
            return ResponseUtils.returnDefaultError();
        }
        // 添加到friends_request表中去
        String msg = userService.sendAddFriendsRequest(userId, friendId);
        if (msg.equals("Already Send Add Friends Request")) {
            return ResponseUtils.returnError(400, msg);
        }
        return ResponseUtils.returnSuccess(msg);
    }

    @ApiOperation(value = "获取好友请求列表", notes = "获取好友请求列表")
    @PostMapping(value = "/addFriendsReqList")
    public Response addFriendsReqList(
            @ApiParam(name = "userId", value = "用户id") @RequestParam(value = "userId") String userId
    ) throws Exception{
        log.info("获取好友请求列表, userId:{}", userId);
        if (StringUtils.isBlank(userId)) {
            return ResponseUtils.returnDefaultError();
        }
        // 添加到friends_request表中去
        List<FriendRequestDto> dtoList = userService.sendList(userId);
        return ResponseUtils.returnSuccess(dtoList);
    }

    @ApiOperation(value = "操作好友请求", notes = "获取好友请求列表")
    @PostMapping(value = "/operFriendRequest")
    public Response addFriendsReqList(
            @ApiParam(name = "acceptUserId", value = "接收方用户id") @RequestParam(value = "acceptUserId") String acceptUserId,
            @ApiParam(name = "sendUserId", value = "发送方用户id") @RequestParam(value = "sendUserId") String sendUserId,
            @ApiParam(name = "operType", value = "操作类型") @RequestParam(value = "operType") Integer operType
    ) throws Exception{
        log.info("操作好友请求, acceptUserId:{},sendUserId:{},operType:{}", acceptUserId, sendUserId, operType);
        if (StringUtils.isBlank(sendUserId) || StringUtils.isBlank(acceptUserId) || operType == null) {
            return ResponseUtils.returnDefaultError();
        }
        if (operType != 1 && operType != 0) {
            return ResponseUtils.returnDefaultError();
        }
        // 0 忽略,1 接受
        if (operType == 0) {
            // 忽略就直接删除
            userService.deleteAddFriendsReq(sendUserId,acceptUserId);
        }else {
            // 接受就先添加mapping表里,再删除
            userService.insertIntoMapping(sendUserId, acceptUserId);
            userService.deleteAddFriendsReq(sendUserId,acceptUserId);
            // 如果两边都互相发了请求,就要删除两个请求
            userService.deleteAddFriendsReq(acceptUserId,sendUserId);

            // 再拉取一下请求列表
            Channel sendChannel = UserChannelMapping.get(sendUserId);
            if (sendChannel != null) {
                DataContent dataContent = new DataContent();
                dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);
                sendChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent)));
            }

        }
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "通讯录列表", notes = "通讯录列表")
    @PostMapping(value = "/friends/list")
    public Response scanSearch(
            @ApiParam(name = "userId", value = "用户id") @RequestParam(value = "userId") String userId
    ) throws Exception{
        log.info("通讯录列表,userId:{}", userId);
        if (StringUtils.isBlank(userId)) {
            return ResponseUtils.returnDefaultError();
        }
        // 添加到friends_request表中去
        List<FriendsListDto> dtoList = userService.getFriendsList(userId);
        log.info("通讯录列表,搜索结果:{}", JSON.toJSONString(dtoList));
        return ResponseUtils.returnSuccess(dtoList);
    }

    @ApiOperation(value = "未签收消息的列表", notes = "未签收消息的列表")
    @PostMapping(value = "/unReadMsg/list")
    public Response unReadMsgList(
            @ApiParam(name = "userId", value = "用户id") @RequestParam(value = "userId") String userId
    ) throws Exception{
        log.info("未签收消息的列表,userId:{}", userId);
        if (StringUtils.isBlank(userId)) {
            return ResponseUtils.returnDefaultError();
        }
        // 添加到friends_request表中去
        List<ChatMsg> dtoList = userService.getUnReadMsgList(userId);
        return ResponseUtils.returnSuccess(dtoList);
    }



}
