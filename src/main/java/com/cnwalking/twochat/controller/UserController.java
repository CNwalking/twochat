package com.cnwalking.twochat.controller;

import com.cnwalking.twochat.common.Response;
import com.cnwalking.twochat.dataobject.dto.UserDto;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.service.UserService;
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

@Slf4j
@Api(tags = "UserController", description = "用户模块")
@RestController("UserController")
@RequestMapping("/user")
public class UserController {

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
        log.info("用户登录, loginName:{}, password:{}, cid:{}", username, password, cid);
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


}
