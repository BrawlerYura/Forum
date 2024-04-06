package org.example.category.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.CategoryDto;
import org.example.category.model.requestBody.CategoryCreateRequestBody;
import org.example.category.model.requestBody.CategoryUpdateRequestBody;
import org.example.category.service.CategoryService;
import org.example.common.exceptions.BadRequestException;
import org.example.common.exceptions.ForbiddenException;
import org.example.common.exceptions.NotFoundException;
import org.example.common.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@RequestBody CategoryCreateRequestBody categoryCreateRequestBody, Authentication authentication) throws UserNotFoundException, BadRequestException {
        return ResponseEntity.ok().body(categoryService.createCategory(categoryCreateRequestBody, authentication));
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody CategoryUpdateRequestBody categoryUpdateRequestBody, Authentication authentication) throws UserNotFoundException, ForbiddenException, BadRequestException {
        return ResponseEntity.ok().body(categoryService.updateCategory(categoryId, categoryUpdateRequestBody, authentication));
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") String categoryId, Authentication authentication) throws UserNotFoundException, ForbiddenException, BadRequestException {
        return ResponseEntity.ok().body(categoryService.deleteCategory(categoryId, authentication));
    }

    @GetMapping("/getCategoriesStructure")
    public ResponseEntity<List<CategoryDto>> getCategoriesStructure() throws NotFoundException {
        return ResponseEntity.ok().body(categoryService.getCategoriesStructure());
    }

}
