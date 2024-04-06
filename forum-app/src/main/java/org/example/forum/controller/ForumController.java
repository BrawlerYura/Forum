package org.example.forum.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.FoundElementDto;
import org.example.common.enums.FoundElementType;
import org.example.forum.service.ForumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/forum")
public class ForumController {

    private final ForumService forumService;

    @GetMapping("/findBySubstring")
    public ResponseEntity<Page<FoundElementDto>> findElementBySubstring(
            @RequestParam("substring") String substring,
            @RequestParam(name = "foundElementTypeList", required = false) List<FoundElementType> foundElementTypeList,
            @PageableDefault(size = 10, page = 0, sort = "createDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok().body(forumService.findElementBySubstring(
                substring,
                pageable,
                foundElementTypeList != null ? foundElementTypeList : new ArrayList<>()
        ));
    }

}
