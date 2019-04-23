package com.example.generator.base.controller;

import com.example.generator.base.XbootBaseController;
import com.example.generator.base.entity.User;
import com.example.generator.base.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Czs
 */
@Slf4j
@RestController
@Api(description = "first管理接口")
@RequestMapping("/xboot/user")
@Transactional
public class UserController extends XbootBaseController<User, String> {

    @Autowired
    private UserService userService;

    @Override
    public UserService getService() {
        return userService;
    }

}
