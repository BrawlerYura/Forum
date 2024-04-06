package org.example.common.client;

import org.example.common.dto.FoundElementDto;
import org.example.common.dto.ThemeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

import static org.example.common.security.SecurityConst.HEADER_API_KEY;

@FeignClient(name = "theme-service", url = "http://localhost:8082")
@Repository
public interface ThemeServiceClient {

    @GetMapping("/integration/theme/check/{themeId}")
    Boolean checkThemeById(@PathVariable("themeId") UUID themeId, @RequestHeader(HEADER_API_KEY) String apiKey);

    @GetMapping("/integration/theme/getTheme/{themeId}")
    ThemeDto getThemeById(@PathVariable("themeId") UUID themeId, @RequestHeader(HEADER_API_KEY) String apiKey);

    @GetMapping("/integration/theme/searchForThemes/{substring}")
    List<FoundElementDto> searchForThemes(@PathVariable("substring") String substring, @RequestHeader(HEADER_API_KEY) String apiKey);

}