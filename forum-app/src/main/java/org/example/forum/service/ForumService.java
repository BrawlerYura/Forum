package org.example.forum.service;

import lombok.RequiredArgsConstructor;
import org.example.common.client.CategoryServiceClient;
import org.example.common.client.MessageServiceClient;
import org.example.common.client.ThemeServiceClient;
import org.example.common.dto.FoundElementDto;
import org.example.common.enums.FoundElementType;
import org.example.common.security.props.SecurityProps;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final CategoryServiceClient categoryServiceClient;
    private final ThemeServiceClient themeServiceClient;
    private final MessageServiceClient messageServiceClient;
    private final SecurityProps securityProps;

    public Page<FoundElementDto> findElementBySubstring(
            String substring,
            Pageable pageable,
            List<FoundElementType> foundElementTypeList
    ) {

        List<FoundElementDto> foundElementDtoList = new ArrayList<>();

        if (foundElementTypeList.contains(FoundElementType.CATEGORY) || foundElementTypeList.isEmpty()) {
            List<FoundElementDto> foundElementsInCategories = categoryServiceClient.searchForCategories(substring, securityProps.getIntegrations().getApiKey());
            foundElementDtoList.addAll(foundElementsInCategories);
        }

        if (foundElementTypeList.contains(FoundElementType.THEME) || foundElementTypeList.isEmpty()) {
            List<FoundElementDto> foundElementsInThemes = themeServiceClient.searchForThemes(substring, securityProps.getIntegrations().getApiKey());
            foundElementDtoList.addAll(foundElementsInThemes);
        }

        if (foundElementTypeList.contains(FoundElementType.MESSAGE) || foundElementTypeList.isEmpty()) {
            List<FoundElementDto> foundElementsInMessages = messageServiceClient.searchForMessages(substring, securityProps.getIntegrations().getApiKey());
            foundElementDtoList.addAll(foundElementsInMessages);
        }

        foundElementDtoList.sort(Comparator.comparing(FoundElementDto::getCreateDateTime).reversed());
        return new PageImpl<>(foundElementDtoList, pageable, foundElementDtoList.size());
    }

}
