package org.example.common.client;

import org.example.common.dto.FoundElementDto;
import org.example.common.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

import static org.example.common.security.SecurityConst.HEADER_API_KEY;

@FeignClient(name = "message-service", url = "http://localhost:8083")
@Repository
public interface MessageServiceClient {

    @GetMapping("/integration/message/searchForMessages/{substring}")
    List<FoundElementDto> searchForMessages(@PathVariable("substring") String substring, @RequestHeader(HEADER_API_KEY) String apiKey);

}