package org.example.theme.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.CategoryDto;
import org.example.common.dto.FoundElementDto;
import org.example.common.dto.ThemeDto;
import org.example.common.exceptions.NotFoundException;
import org.example.theme.service.ThemeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.example.common.security.SecurityConst.HEADER_API_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integration/theme")
public class IntegrationThemeController {

    private final ThemeService themeService;

    @GetMapping("/check/{themeId}")
    private Boolean checkThemeById(@PathVariable("themeId") UUID themeId) {
        return themeService.checkThemeById(themeId);
    }

    @GetMapping("/getTheme/{themeId}")
    public ThemeDto getThemeById(@PathVariable("themeId") UUID themeId) throws NotFoundException {
        return themeService.getThemeById(themeId);
    }

    @GetMapping("/searchForThemes/{substring}")
    public List<FoundElementDto> searchForThemes(@PathVariable("substring") String substring) throws NotFoundException {
        return themeService.searchForThemes(substring);
    }

}
