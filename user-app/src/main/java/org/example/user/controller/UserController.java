package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.model.dto.TokenDto;
import org.example.common.exceptions.*;
import org.example.user.model.requestBody.UserCreateRequestBody;
import org.example.user.model.requestBody.UserCredentialsRequestBody;
import org.example.user.model.requestBody.UserUpdateRequestBody;
import org.example.user.service.AuthenticationService;
import org.example.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final AuthenticationService authenticationService;

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@RequestBody UserCreateRequestBody user) throws UserAlreadyExistException, UnauthorizedException, BadRequestException {
        return ResponseEntity.ok().body(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserCredentialsRequestBody user) throws UserAlreadyExistException, UnauthorizedException, BadRequestException {
        return ResponseEntity.ok().body(userService.login(user));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") UUID userId, @RequestBody UserUpdateRequestBody userUpdateRequestBody, Authentication authentication) throws UserNotFoundException, ForbiddenException {
        return ResponseEntity.ok().body(userService.updateUser(userId, userUpdateRequestBody, authentication));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId, Authentication authentication) throws UserNotFoundException, ForbiddenException {
        return ResponseEntity.ok().body(userService.deleteUser(userId, authentication));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) throws UserNotFoundException, ForbiddenException {
        return ResponseEntity.ok().body(userService.logout(authentication));
    }
}
