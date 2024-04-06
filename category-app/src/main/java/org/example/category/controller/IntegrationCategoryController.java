package org.example.category.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.CategoryDto;
import org.example.category.service.CategoryService;
import org.example.common.dto.CategoryWithoutChildDto;
import org.example.common.dto.FoundElementDto;
import org.example.common.exceptions.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.example.common.security.SecurityConst.HEADER_API_KEY;

@RestController
@RequestMapping("/integration/category")
@RequiredArgsConstructor
public class IntegrationCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/check/{categoryId}")
    public Boolean checkCategoryById(@PathVariable("categoryId") UUID categoryId) {
        return categoryService.checkCategoryById(categoryId);
    }

    @GetMapping("/getCategory/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable("categoryId") UUID categoryId) throws NotFoundException {
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/getCategoryWithoutChild/{categoryId}")
    public CategoryWithoutChildDto getCategoryWithoutChildById(@PathVariable("categoryId") UUID categoryId) throws NotFoundException {
        return categoryService.getCategoryWithoutChildById(categoryId);
    }

    @GetMapping("/searchForCategories/{substring}")
    public List<FoundElementDto> searchForCategories(@PathVariable("substring") String substring) {
        return categoryService.searchForCategories(substring);
    }

}
