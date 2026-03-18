package com.petboarding.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.Result;
import com.petboarding.entity.User;
import com.petboarding.exception.BusinessException;
import com.petboarding.service.UserService;
import com.petboarding.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new BusinessException("Username and password are required");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userService.count(wrapper) > 0) {
            throw new BusinessException("Username already exists");
        }

        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        user.setPoints(0);
        userService.save(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new BusinessException("Username and password are required");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        wrapper.eq(User::getPassword, DigestUtil.md5Hex(user.getPassword()));
        User dbUser = userService.getOne(wrapper);

        if (dbUser == null) {
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtUtils.generateToken(dbUser.getId(), dbUser.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        dbUser.setPassword(null);
        result.put("user", dbUser);
        return Result.success(result);
    }

    @GetMapping("/info")
    public Result<?> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        user.setPassword(null);
        user.setRole(null);
        userService.updateById(user);
        return Result.success();
    }

    @PutMapping("/verify")
    public Result<?> verify(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setRealName(user.getRealName());
        updateUser.setIdCard(user.getIdCard());
        updateUser.setVerified(1);
        userService.updateById(updateUser);
        return Result.success();
    }
}
