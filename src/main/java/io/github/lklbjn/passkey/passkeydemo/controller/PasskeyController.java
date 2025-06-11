package io.github.lklbjn.passkey.passkeydemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import io.github.lklbjn.passkey.passkeydemo.model.entity.User;
import io.github.lklbjn.passkey.passkeydemo.model.request.PasskeyRegistrationRequest;
import io.github.lklbjn.passkey.passkeydemo.service.PasskeyAuthorizationService;
import io.github.lklbjn.passkey.passkeydemo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lklbjn
 */
@Slf4j
@RestController
@RequestMapping("/passkey")
@RequiredArgsConstructor
public class PasskeyController {

    private final UserService userService;
    private final PasskeyAuthorizationService passkeyAuthorizationService;

    /**
     * 获取 Passkey 注册选项
     * @param userId 用户ID
     * @return 注册选项JSON
     */
    @GetMapping("/registration/options/{userId}")
    public ResponseEntity<String> getPasskeyRegistrationOptions(@PathVariable Long userId) {
        try {
            String options = passkeyAuthorizationService.startPasskeyRegistration(userId);
            if (options.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate registration options");
            }
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            log.error("Error generating passkey registration options", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Passkey 注册
     * @param request
     * @return
     */
    @PostMapping("/registration")
    public ResponseEntity<String> verifyPasskeyRegistration(@RequestBody PasskeyRegistrationRequest request) {
        try {
            passkeyAuthorizationService.finishPasskeyRegistration(request);
            return ResponseEntity.ok("Passkey registration successful");
        } catch (IOException e) {
            log.error("IO error during passkey registration", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credential format");
        } catch (RegistrationFailedException e) {
            log.error("Registration failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during passkey registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * 获取 Passkey 登录选项
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/assertion/options")
    public  ResponseEntity<String> getPasskeyAssertionOptions(HttpServletRequest httpServletRequest) {
        try {
            String sessionId = httpServletRequest.getSession().getId();
            String options = passkeyAuthorizationService.startPasskeyAssertion(sessionId);
            return ResponseEntity.ok(options);
        } catch (JsonProcessingException e) {
            log.error("Error generating passkey assertion options", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during passkey assertion options", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Passkey 登录
     * @param httpServletRequest
     * @param httpServletResponse
     * @param credential
     * @return
     */
    @PostMapping("/assertion")
    public ResponseEntity<Object> verifyPasskeyAssertion(HttpServletRequest httpServletRequest,
                                                    HttpServletResponse httpServletResponse,
                                                    @RequestBody String credential) {
        try {
            String sessionId = httpServletRequest.getSession().getId();
            var auth = passkeyAuthorizationService.finishPasskeyAssertion(sessionId, credential);

            // 获取用户信息
            User user = userService.getUserByUserId(auth.getUserId());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // 登录用户（这里需要根据你的认证系统进行调整）
            loginUser(user, httpServletRequest, httpServletResponse);

            // 返回用户基本信息（可以根据需要调整）
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("describe", auth.getDescribe());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("IO error during passkey assertion", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credential format");
        } catch (AssertionFailedException e) {
            log.error("Assertion failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during passkey assertion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * 登录用户（需要根据你的认证系统实现）
     */
    private void loginUser(User user, HttpServletRequest request, HttpServletResponse response) {
        // 这里实现用户登录逻辑
        // 例如：使用Spring Security
        // SecurityContextHolder.getContext().setAuthentication(
        //     new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        // );

        // 或者设置session
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

        // 或者设置JWT令牌等
        // String token = jwtService.generateToken(user);
        // response.setHeader("Authorization", "Bearer " + token);
    }

}