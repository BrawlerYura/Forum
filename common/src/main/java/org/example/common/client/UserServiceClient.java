package org.example.common.client;

import org.example.common.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

import static org.example.common.security.SecurityConst.HEADER_API_KEY;

@FeignClient(name = "user-service", url = "http://localhost:8080")
@Repository
public interface UserServiceClient {

    @GetMapping("/integration/user/check/{userId}")
    Boolean checkUserById(@PathVariable("userId") UUID userId, @RequestHeader(HEADER_API_KEY) String apiKey);

    @GetMapping("/integration/user/getProfile/{userId}")
    UserDto getUserById(@PathVariable("userId") UUID userId, @RequestHeader(HEADER_API_KEY) String apiKey);

}