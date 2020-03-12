package com.cnwalking.twochat.controller;

import com.alibaba.fastjson.JSONObject;
import com.cnwalking.twochat.common.Response;
import com.cnwalking.twochat.common.ResponseException;
import com.cnwalking.twochat.common.StatusCodeEnu;
import com.cnwalking.twochat.dataobject.dto.UserDto;
import com.cnwalking.twochat.dataobject.entity.User;
import com.cnwalking.twochat.service.UserService;
import com.cnwalking.twochat.utils.MD5Encrypt;
import com.cnwalking.twochat.utils.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

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
            @ApiParam(name = "login_name", value = "用户名") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password) {
        log.info("用户登录, loginName:{}, password:{}", loginName, password);
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        if (userService.userNameIsExist(loginName)) {
            // 存在 就登录
            if (!userService.checkPswd(loginName, password)) {
                return ResponseUtils.returnDefaultError();
            }
        }else {
            // 不存在 就注册
            User user = userService.register(loginName, password);
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(user,dto);
            return ResponseUtils.returnSuccess(dto);
        }
        return ResponseUtils.returnDefaultSuccess();
    }


}
