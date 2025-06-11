package io.github.lklbjn.passkey.passkeydemo.controller;

import io.github.lklbjn.passkey.passkeydemo.model.request.UserAddRequest;
import io.github.lklbjn.passkey.passkeydemo.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author lklbjn
 * @DATE 2025/6/10 15:18
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody UserAddRequest request) {
        try {
            userService.addUser(request);
            return ResponseEntity.ok("用户创建成功！");
        } catch (Exception e) {
            log.error("用户创建异常：", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/userHandle/regenerate/{userId}")
    public ResponseEntity<String> regenerateUserHandle(@PathVariable Long userId) {
        try {
            userService.refreshUserHandle(userId);
            return ResponseEntity.ok("用户Handle重置成功！");
        } catch (Exception e) {
            log.error("用户Handle重置异常：", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
