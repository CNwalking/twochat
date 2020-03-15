package com.cnwalking.twochat.controller;

import com.cnwalking.twochat.common.Response;
import com.cnwalking.twochat.dataobject.dto.UserDto;
import com.cnwalking.twochat.dataobject.dto.UserVo;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.service.UserService;
import com.cnwalking.twochat.utils.FastDFSClient;
import com.cnwalking.twochat.utils.FileUtils;
import com.cnwalking.twochat.utils.ResponseUtils;
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
        if (StringUtils.isBlank(nickName)) {
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

}
