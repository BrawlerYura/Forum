package org.example.theme.controller;


import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.common.exceptions.BadRequestException;
import org.example.common.exceptions.ForbiddenException;
import org.example.common.exceptions.UserNotFoundException;
import org.example.common.dto.ThemeDto;
import org.example.theme.model.requestBody.CreateThemeRequestBody;
import org.example.theme.model.requestBody.UpdateThemeRequestBody;
import org.example.theme.service.ThemeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/theme")
public class ThemeController {

    private final ThemeService themeService;

    @PostMapping("/create/{categoryId}")
    public ResponseEntity<String> createTheme(@PathVariable("categoryId") @NotNull UUID categoryId, @RequestBody CreateThemeRequestBody createThemeRequestBody, Authentication authentication) throws UserNotFoundException, BadRequestException {
        return ResponseEntity.ok().body(themeService.createTheme(categoryId, createThemeRequestBody, authentication));
    }

    @PutMapping("/update/{themeId}")
    public ResponseEntity<Void> updateTheme(@PathVariable("themeId") UUID themeId, @RequestBody UpdateThemeRequestBody updateThemeRequestBody, Authentication authentication) throws UserNotFoundException, ForbiddenException, BadRequestException {
        return ResponseEntity.ok().body(themeService.updateTheme(themeId, updateThemeRequestBody, authentication));
    }

    @DeleteMapping("/delete/{themeId}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("themeId") UUID themeId, Authentication authentication) throws UserNotFoundException, ForbiddenException, BadRequestException {
        return ResponseEntity.ok().body(themeService.deleteTheme(themeId, authentication));
    }

    @GetMapping("/getThemeListInCategory/{categoryId}")
    public ResponseEntity<Page<ThemeDto>> getThemeListInCategory(
            @PathVariable("categoryId") UUID categoryId,
            @PageableDefault(size = 10, page = 0, sort = "createDateTime", direction = Sort.Direction.DESC) Pageable pageable
            ) throws BadRequestException {
        return ResponseEntity.ok().body(themeService.getThemeListInCategory(categoryId, pageable));
    }

    @GetMapping("/getThemeList")
    public ResponseEntity<Page<ThemeDto>> getThemeList(@PageableDefault(size = 10, page = 0, sort = "createDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(themeService.getThemeList(pageable));
    }
}
