package org.example.common.client;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.CategoryDto;
import org.example.common.dto.CategoryWithoutChildDto;
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

@FeignClient(name = "category-service", url = "http://localhost:8081")
@Repository
public interface CategoryServiceClient {

    @GetMapping("/integration/category/check/{categoryId}")
    Boolean checkCategoryById(@PathVariable("categoryId") UUID categoryId, @RequestHeader(HEADER_API_KEY) String apiKey);

    @GetMapping("/integration/category/getCategory/{categoryId}")
    CategoryDto getCategoryById(@PathVariable("categoryId") UUID categoryId, @RequestHeader(HEADER_API_KEY) String apiKey);

    @GetMapping("/integration/category/getCategoryWithoutChild/{categoryId}")
    CategoryWithoutChildDto getCategoryWithoutChildById(@PathVariable("categoryId") UUID categoryId, @RequestHeader(HEADER_API_KEY) String apiKey);

    @GetMapping("/integration/category/searchForCategories/{substring}")
    List<FoundElementDto> searchForCategories(@PathVariable("substring") String substring, @RequestHeader(HEADER_API_KEY) String apiKey);

}
