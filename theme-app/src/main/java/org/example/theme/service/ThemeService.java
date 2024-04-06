package org.example.theme.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.example.common.dto.*;
import org.example.common.client.CategoryServiceClient;
import org.example.common.client.UserServiceClient;
import org.example.common.enums.FoundElementType;
import org.example.common.exceptions.BadRequestException;
import org.example.common.exceptions.ForbiddenException;
import org.example.common.exceptions.NotFoundException;
import org.example.common.exceptions.UserNotFoundException;
import org.example.common.security.JwtUserData;
import org.example.common.security.props.SecurityProps;
import org.example.theme.model.entity.ThemeEntity;
import org.example.theme.model.requestBody.CreateThemeRequestBody;
import org.example.theme.model.requestBody.UpdateThemeRequestBody;
import org.example.theme.repository.ThemeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final UserServiceClient userServiceClient;
    private final CategoryServiceClient categoryServiceClient;
    private final SecurityProps securityProps;

    public String createTheme(UUID categoryId, CreateThemeRequestBody createThemeRequestBody, Authentication authentication) throws UserNotFoundException, BadRequestException {
        val userId = getUserId(authentication);
        val categoryDto = categoryServiceClient.getCategoryById(categoryId, securityProps.getIntegrations().getApiKey());

        ThemeEntity themeEntity = themeRepository.save(
                new ThemeEntity(
                        UUID.randomUUID(),
                        createThemeRequestBody.getThemeName(),
                        categoryDto.getId(),
                        Instant.now(),
                        null,
                        userId
                )
        );

        return themeEntity.getId().toString();
    }

    public Void updateTheme(UUID themeId, UpdateThemeRequestBody updateThemeRequestBody, Authentication authentication) throws BadRequestException, ForbiddenException {
        val userId = getUserId(authentication);

        var themeEntity = themeRepository.findById(themeId).orElse(null);
        if(themeEntity == null) {
            throw new BadRequestException("Invalid themeId: theme with id " + themeId + " doesn't exists");
        }

        if(!userId.equals(themeEntity.getCreatorId())) {
            throw new ForbiddenException("You can't change theme with id: " + themeId);
        }

        var categoryId = themeEntity.getCategoryId();
        if(updateThemeRequestBody.getCategoryId() != null) {
            if(!categoryServiceClient.checkCategoryById(categoryId, securityProps.getIntegrations().getApiKey())) {
                throw new BadRequestException("category with id " + categoryId + " not found");
            }
            categoryId = updateThemeRequestBody.getCategoryId();
        }

        themeRepository.save(
                new ThemeEntity(
                        themeEntity.getId(),
                        updateThemeRequestBody.getThemeName(),
                        categoryId,
                        themeEntity.getCreateDateTime(),
                        Instant.now(),
                        themeEntity.getCreatorId()
                )
        );

        return null;
    }

    public Void deleteTheme(UUID themeId, Authentication authentication) throws BadRequestException, ForbiddenException {
        val userId = getUserId(authentication);

        var themeEntity = themeRepository.findById(themeId).orElse(null);
        if(themeEntity == null) {
            throw new BadRequestException("Invalid themeId: theme with id " + themeId + " doesn't exists");
        }

        if(!userId.equals(themeEntity.getCreatorId())) {
            throw new ForbiddenException("You can't delete theme with id: " + themeId);
        }

        themeRepository.delete(themeEntity);

        return null;
    }

    public Page<ThemeDto> getThemeListInCategory(UUID categoryId, Pageable pageable) throws BadRequestException {

        if(!categoryServiceClient.checkCategoryById(categoryId, securityProps.getIntegrations().getApiKey())) {
            throw new BadRequestException("category with id " + categoryId + " not found");
        }

        Page<ThemeEntity> themeEntityList = themeRepository.findAllByCategoryId(pageable, categoryId);

        return toThemeDtoPage(themeEntityList);
    }

    public Page<ThemeDto> getThemeList(Pageable pageable) {

        Page<ThemeEntity> themeEntityList = themeRepository.findAll(pageable);

        return toThemeDtoPage(themeEntityList);
    }

    public Page<ThemeDto> toThemeDtoPage(Page<ThemeEntity> themesPage) {
        List<ThemeDto> themeDtoList = themesPage.getContent().stream()
                .map(this::toThemeDto)
                .collect(Collectors.toList());
        return new PageImpl<>(themeDtoList, themesPage.getPageable(), themesPage.getTotalElements());
    }

    public ThemeDto toThemeDto(ThemeEntity theme) {
        UserDto creatorDto = userServiceClient.getUserById(theme.getCreatorId(), securityProps.getIntegrations().getApiKey());
        CategoryDto categoryDto = categoryServiceClient.getCategoryById(theme.getCategoryId(), securityProps.getIntegrations().getApiKey());

        return new ThemeDto(
                theme.getId(),
                theme.getThemeName(),
                new CategoryWithoutChildDto(
                        categoryDto.getId(),
                        categoryDto.getCategoryName(),
                        categoryDto.getCreateDateTime(),
                        categoryDto.getUpdateDateTime(),
                        categoryDto.getCreator()
                ),
                theme.getCreateDateTime(),
                theme.getUpdateDateTime(),
                creatorDto
        );
    }

    private UUID getUserId(Authentication authentication) {
        val userData = (JwtUserData) authentication.getPrincipal();
        userServiceClient.checkUserById(userData.getId(), securityProps.getIntegrations().getApiKey());
        return userData.getId();
    }

    public ThemeDto getThemeById(UUID themeId) throws NotFoundException {
        val themeEntity = themeRepository.findById(themeId).orElse(null);
        if(themeEntity == null) {
            throw new NotFoundException("theme with id " + themeId + " not found");
        }
        return new ThemeDto(
                themeEntity.getId(),
                themeEntity.getThemeName(),
                categoryServiceClient.getCategoryWithoutChildById( themeEntity.getCategoryId(), securityProps.getIntegrations().getApiKey()),
                themeEntity.getCreateDateTime(),
                themeEntity.getUpdateDateTime(),
                userServiceClient.getUserById(themeEntity.getCreatorId(), securityProps.getIntegrations().getApiKey())
        );
    }

    public List<FoundElementDto> searchForThemes(String substring) {

        List<FoundElementDto> foundElementDtoList = new ArrayList<>();

        List<ThemeEntity> themeEntityList = themeRepository.findAllByThemeNameContainingIgnoreCase(substring);
        for (ThemeEntity themeEntity : themeEntityList) {
            UserDto creator = userServiceClient.getUserById(themeEntity.getCreatorId(), securityProps.getIntegrations().getApiKey());
            foundElementDtoList.add(
                    new FoundElementDto(
                            themeEntity.getId(),
                            FoundElementType.THEME,
                            themeEntity.getThemeName(),
                            themeEntity.getCreateDateTime(),
                            creator
                    )
            );
        }
        return foundElementDtoList;
    }

    public Boolean checkThemeById(UUID id) {
        return themeRepository.existsById(id);
    }
}
