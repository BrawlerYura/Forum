package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.UserDto;
import org.example.common.exceptions.ForbiddenException;
import org.example.common.exceptions.UserNotFoundException;
import org.example.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/integration/user")
@RequiredArgsConstructor
public class IntegrationUserController {

    private final UserService userService;

    @GetMapping("/check/{userId}")
    public Boolean check(@PathVariable("userId") UUID userId) {
        return userService.isUserExists(userId);
    }

    @GetMapping("/getProfile/{userId}")
    public UserDto getProfile(@PathVariable("userId") UUID userId) throws UserNotFoundException, ForbiddenException {
        return userService.getProfile(userId);
    }
}