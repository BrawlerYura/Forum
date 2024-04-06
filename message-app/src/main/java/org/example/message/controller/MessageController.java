package org.example.message.controller;


import lombok.RequiredArgsConstructor;
import org.example.common.exceptions.BadRequestException;
import org.example.common.exceptions.ForbiddenException;
import org.example.common.exceptions.UserNotFoundException;
import org.example.message.model.dto.FetchMessagesDto;
import org.example.message.model.dto.MessageDto;
import org.example.message.model.requestBody.CreateMessageRequestBody;
import org.example.message.model.requestBody.UpdateMessageRequestBody;
import org.example.message.service.MessageService;
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
@RequestMapping("api/message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/create/{themeId}")
    public ResponseEntity<String> createMessage(@PathVariable("themeId") UUID themeId, @RequestBody CreateMessageRequestBody createMessageRequestBody, Authentication authentication) throws UserNotFoundException, BadRequestException {
        return ResponseEntity.ok().body(messageService.createMessage(themeId, createMessageRequestBody, authentication));
    }

    @PutMapping("/update/{messageId}")
    public ResponseEntity<String> updateMessage(@PathVariable("messageId") UUID messageId, @RequestBody UpdateMessageRequestBody updateMessageRequestBody, Authentication authentication) throws UserNotFoundException, ForbiddenException, BadRequestException {
        return ResponseEntity.ok().body(messageService.updateMessage(messageId, updateMessageRequestBody, authentication));
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId, Authentication authentication) throws UserNotFoundException, ForbiddenException, BadRequestException {
        return ResponseEntity.ok().body(messageService.deleteMessage(messageId, authentication));
    }

    @GetMapping("/getMessagesInTheme/{themeId}")
    public ResponseEntity<Page<MessageDto>> getMessagesInTheme(
            @PathVariable("themeId") UUID themeId,
            @PageableDefault(size = 10, page = 0, sort = "createDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) throws UserNotFoundException, ForbiddenException, BadRequestException {
        return ResponseEntity.ok().body(messageService.getMessagesInTheme(themeId, pageable));
    }

    @PostMapping("/getMessages")
    public ResponseEntity<Page<MessageDto>> getMessagesWithFilter(
            @RequestBody FetchMessagesDto messagesDto,
            @PageableDefault(size = 10, page = 0, sort = "createDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) throws UserNotFoundException, ForbiddenException, BadRequestException {

        return ResponseEntity.ok().body(messageService.getMessagesWithFilter(
                        pageable,
                        messagesDto
                )
        );
    }
}
