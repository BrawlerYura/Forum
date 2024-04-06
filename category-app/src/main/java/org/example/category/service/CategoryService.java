package org.example.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.common.dto.CategoryDto;
import org.example.category.model.entity.CategoryEntity;
import org.example.category.model.requestBody.CategoryCreateRequestBody;
import org.example.category.model.requestBody.CategoryUpdateRequestBody;
import org.example.category.repository.CategoryRepository;
import org.example.common.client.UserServiceClient;
import org.example.common.dto.CategoryWithoutChildDto;
import org.example.common.dto.FoundElementDto;
import org.example.common.dto.UserDto;
import org.example.common.enums.FoundElementType;
import org.example.common.exceptions.BadRequestException;
import org.example.common.exceptions.ForbiddenException;
import org.example.common.exceptions.NotFoundException;
import org.example.common.exceptions.UserNotFoundException;
import org.example.common.security.JwtUserData;
import org.example.common.security.props.SecurityProps;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableFeignClients
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserServiceClient userServiceClient;
    private final SecurityProps securityProps;

    public String createCategory(CategoryCreateRequestBody category, Authentication authentication) throws BadRequestException, UserNotFoundException {
        val userId = getUserId(authentication);

        CategoryEntity parentEntity = null;
        if(category.getParentId() != null) {
            var optionalParentEntity = categoryRepository.findById(category.getParentId());
            if (optionalParentEntity.isEmpty()) {
                throw new BadRequestException("Invalid parentId: category with id " + category.getParentId() + "doesn't exists");
            }
            parentEntity = optionalParentEntity.get();
        }

        CategoryEntity categoryEntity = categoryRepository.save(
                new CategoryEntity(
                        UUID.randomUUID(),
                        category.getCategoryName(),
                        parentEntity,
                        null,
                        Instant.now(),
                        null,
                        userId
                )
        );

        return categoryEntity.getId().toString();
    }

    public String updateCategory(String categoryId, CategoryUpdateRequestBody updatedCategory, Authentication authentication) throws UserNotFoundException, BadRequestException, ForbiddenException {
        val userId = getUserId(authentication);

        Optional<CategoryEntity> optionalCategoryEntity = categoryRepository.findById(UUID.fromString(categoryId));
        if(optionalCategoryEntity.isEmpty()) {
            throw new BadRequestException("Invalid categoryId: category with id " + categoryId + "doesn't exists");
        }
        CategoryEntity categoryEntity = optionalCategoryEntity.get();

        if(!categoryEntity.getCreatorId().equals(userId)) {
            throw new ForbiddenException("You can't change category with id: " + categoryId);
        }

        CategoryEntity newCategoryEntity = categoryRepository.save(
                new CategoryEntity(
                        categoryEntity.getId(),
                        updatedCategory.getCategoryName(),
                        categoryEntity.getParentCategory(),
                        categoryEntity.getChildCategories(),
                        categoryEntity.getCreateDateTime(),
                        Instant.now(),
                        categoryEntity.getCreatorId()
                )
        );

        return newCategoryEntity.getId().toString();
    }

    public Void deleteCategory(String categoryId, Authentication authentication) throws UserNotFoundException, BadRequestException, ForbiddenException {
        val userId = getUserId(authentication);

        var optionalCategoryEntity = categoryRepository.findById(UUID.fromString(categoryId));
        if(optionalCategoryEntity.isEmpty()) {
            throw new BadRequestException("Invalid categoryId: category with id " + categoryId + "doesn't exists");
        }
        CategoryEntity categoryEntity = optionalCategoryEntity.get();

        if(!categoryEntity.getCreatorId().equals(userId)) {
            throw new ForbiddenException("You can't change category with id: " + categoryId);
        }

        categoryRepository.delete(categoryEntity);;
        return null;
    }

    public List<CategoryDto> getCategoriesStructure() throws NotFoundException {
        var CategoriesListEntity = categoryRepository.findAllByParentCategoryIsNull();
        if(CategoriesListEntity.isEmpty()) {
            throw new NotFoundException("There is no categories found");
        }

        return toCategoryDtoList(CategoriesListEntity);
    }

    public List<CategoryDto> toCategoryDtoList(List<CategoryEntity> categories) {
        categories.sort(Comparator.comparing(CategoryEntity::getCategoryName));
        return categories.stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto toCategoryDto(CategoryEntity category) {
        List<CategoryDto> childDtos = Collections.emptyList();
        if (category.getChildCategories() != null && !category.getChildCategories().isEmpty()) {
            childDtos = toCategoryDtoList(category.getChildCategories());
        }
        UserDto userDto = userServiceClient.getUserById(category.getCreatorId(), securityProps.getIntegrations().getApiKey());

        return new CategoryDto(
                category.getId(),
                category.getCategoryName(),
                childDtos,
                category.getCreateDateTime(),
                category.getUpdateDateTime(),
                userDto
        );
    }

    public CategoryDto getCategoryById(UUID categoryId) throws NotFoundException {

        val categoryEntity = categoryRepository.findById(categoryId).orElse(null);
        if(categoryEntity == null) {
            throw new NotFoundException("category with id " + categoryId + " not found");
        }

        return toCategoryDto(categoryEntity);
    }

    public CategoryWithoutChildDto getCategoryWithoutChildById(UUID categoryId) throws NotFoundException {

        val categoryEntity = categoryRepository.findById(categoryId).orElse(null);
        if(categoryEntity == null) {
            throw new NotFoundException("category with id " + categoryId + " not found");
        }

        return toCategoryWithoutChildDto(categoryEntity);
    }

    public CategoryWithoutChildDto toCategoryWithoutChildDto (CategoryEntity category) {
        UserDto creatorDto = userServiceClient.getUserById(category.getCreatorId(), securityProps.getIntegrations().getApiKey());

        return new CategoryWithoutChildDto(
                category.getId(),
                category.getCategoryName(),
                category.getCreateDateTime(),
                category.getUpdateDateTime(),
                creatorDto
        );
    }

    public List<FoundElementDto> searchForCategories(String substring) {
        List<FoundElementDto> foundElementDtoList = new ArrayList<>();

        List<CategoryEntity> categoryResults = categoryRepository.findAllByCategoryNameContainingIgnoreCase(substring);
        for (CategoryEntity category : categoryResults) {
            UserDto creator = userServiceClient.getUserById(category.getCreatorId(), securityProps.getIntegrations().getApiKey());
            foundElementDtoList.add(
                    new FoundElementDto(
                            category.getId(),
                            FoundElementType.CATEGORY,
                            category.getCategoryName(),
                            category.getCreateDateTime(),
                            creator
                    )
            );
        }
        return foundElementDtoList;
    }

    private UUID getUserId(Authentication authentication) {
        val userData = (JwtUserData) authentication.getPrincipal();
        userServiceClient.checkUserById(userData.getId(), securityProps.getIntegrations().getApiKey());
        return userData.getId();
    }

    public Boolean checkCategoryById(UUID id) {
        return categoryRepository.existsById(id);
    }

}
