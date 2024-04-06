package org.example.message.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.FoundElementDto;
import org.example.message.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integration/message")
public class IntegrationMessageController {

    private final MessageService messageService;

    @GetMapping("/searchForMessages/{substring}")
    public List<FoundElementDto> searchForMessages(@PathVariable("substring") String substring) {
        return messageService.searchForMessages(substring);
    }
}
