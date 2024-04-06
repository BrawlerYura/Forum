package org.example.message.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.example.common.client.ThemeServiceClient;
import org.example.common.client.UserServiceClient;
import org.example.common.dto.FoundElementDto;
import org.example.common.dto.ThemeDto;
import org.example.common.dto.UserDto;
import org.example.common.enums.FoundElementType;
import org.example.common.exceptions.BadRequestException;
import org.example.common.exceptions.ForbiddenException;
import org.example.common.exceptions.UserNotFoundException;
import org.example.common.security.JwtUserData;
import org.example.common.security.props.SecurityProps;
import org.example.message.model.dto.MessageDto;
import org.example.message.model.entity.MessageEntity;
import org.example.message.model.requestBody.CreateMessageRequestBody;
import org.example.message.model.requestBody.UpdateMessageRequestBody;
import org.example.message.repository.MessageRepository;
import org.example.message.model.dto.FetchMessagesDto;
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
public class MessageService {

    private final MessageRepository messageRepository;
    private final SecurityProps securityProps;
    private final UserServiceClient userServiceClient;
    private final ThemeServiceClient themeServiceClient;

    public String createMessage(UUID themeId, CreateMessageRequestBody createMessageRequestBody, Authentication authentication) throws UserNotFoundException, BadRequestException {
        val userId = getUserId(authentication);

        if(!themeServiceClient.checkThemeById(themeId, securityProps.getIntegrations().getApiKey())){
            throw new BadRequestException("bad request: theme with id " + themeId + " not found");
        }

        val themeEntity = themeServiceClient.getThemeById(themeId, securityProps.getIntegrations().getApiKey());

        MessageEntity messageEntity = messageRepository.save(
                new MessageEntity(
                        UUID.randomUUID(),
                        createMessageRequestBody.getText(),
                        themeId,
                        Instant.now(),
                        null,
                        userId,
                        themeEntity.getCategory().getId()
                )
        );

        return messageEntity.getId().toString();

    }

    public String updateMessage(UUID messageId, UpdateMessageRequestBody updateMessageRequestBody, Authentication authentication) throws UserNotFoundException, BadRequestException, ForbiddenException {

        val userId = getUserId(authentication);

        var messageEntity = messageRepository.findById(messageId).orElse(null);
        if(messageEntity == null) {
            throw new BadRequestException("Invalid messageId: message with id " + messageId + "doesn't exists");
        }

        if(!userId.equals(messageEntity.getCreatorId())) {
            throw new ForbiddenException("You can't change message with id: " + messageId);
        }

        UUID themeId = messageEntity.getThemeId();
        if(updateMessageRequestBody.getThemeId() != null) {
            if(!themeServiceClient.checkThemeById(themeId, securityProps.getIntegrations().getApiKey())){
                throw new BadRequestException("bad request: theme with id " + themeId + " not found");
            }
            themeId = messageEntity.getThemeId();
        }

        val themeEntity = themeServiceClient.getThemeById(themeId, securityProps.getIntegrations().getApiKey());

        messageRepository.save(
                new MessageEntity(
                        messageEntity.getId(),
                        updateMessageRequestBody.getText(),
                        themeId,
                        messageEntity.getCreateDateTime(),
                        Instant.now(),
                        userId,
                        themeEntity.getCategory().getId()
                )
        );

        return messageEntity.getId().toString();
    }

    public Void deleteMessage(UUID messageId, Authentication authentication) throws UserNotFoundException, BadRequestException, ForbiddenException {

        val userId = getUserId(authentication);

        var optionalMessageEntity = messageRepository.findById(messageId);
        if(optionalMessageEntity.isEmpty()) {
            throw new BadRequestException("Invalid messageId: message with id " + messageId + "doesn't exists");
        }
        MessageEntity messageEntity = optionalMessageEntity.get();

        if(!messageEntity.getCreatorId().equals(userId)) {
            throw new ForbiddenException("You can't delete message with id: " + messageId);
        }

        messageRepository.delete(messageEntity);;
        return null;
    }

    public Page<MessageDto> getMessagesInTheme(UUID themeId, Pageable pageable) throws BadRequestException {

        if(!themeServiceClient.checkThemeById(themeId, securityProps.getIntegrations().getApiKey())){
            throw new BadRequestException("bad request: theme with id " + themeId + " not found");
        }

        Page<MessageEntity> messageEntityPage = messageRepository.findAllByThemeId(pageable, themeId);

        return toMessageDtoPage(messageEntityPage);
    }

    public Page<MessageDto> getMessagesWithFilter(
            Pageable pageable,
            FetchMessagesDto messagesDto
    ) throws BadRequestException {

        List<MessageDto> messageDtoList = messageRepository
                .findAll(((root, query, cb) -> {
                    var predicates = new ArrayList<>();
                    messagesDto.getFields().forEach((fieldName, fieldValue) -> {
                        switch (fieldName) {
                            case "text":
                                predicates.add(cb.like(root.get(fieldName), "%" + fieldValue + "%"));
                                break;
                            case "startDate":
                                Instant startDate = Instant.parse(fieldValue);
                                predicates.add(cb.greaterThanOrEqualTo(root.get("createDateTime"), startDate));
                                break;
                            case "endDate":
                                Instant endDate = Instant.parse(fieldValue);
                                predicates.add(cb.lessThanOrEqualTo(root.get("createDateTime"), endDate));
                                break;
                            case "creatorId", "themeId", "categoryId":
                                try {
                                    UUID id = UUID.fromString(fieldValue);
                                    predicates.add(cb.equal(root.get(fieldName), id));
                                } catch (Exception e){
                                    try {
                                        throw new BadRequestException("Invalid id value \"" + fieldValue + "\"");
                                    } catch (BadRequestException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                break;
                            default:
                                try {
                                    throw new BadRequestException("Bad field with name " + fieldName);
                                } catch (BadRequestException e) {
                                    throw new RuntimeException(e);
                                }
                        }
                    });

                    return cb.and(predicates.toArray(new Predicate[0]));
                }))
                .stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());

        return new PageImpl<>(messageDtoList, pageable, messageDtoList.size());
    }

    public Page<MessageDto> toMessageDtoPage(Page<MessageEntity> messagePage) {
        List<MessageDto> messageDtoList = messagePage.getContent().stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
        return new PageImpl<>(messageDtoList, messagePage.getPageable(), messagePage.getTotalElements());
    }

    public List<MessageDto> toMessageDtoList(List<MessageEntity> messageEntityList) {
        return messageEntityList.stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    private MessageDto toMessageDto(MessageEntity message) {
        UserDto creatorDto = userServiceClient.getUserById(message.getCreatorId(), securityProps.getIntegrations().getApiKey());
        ThemeDto themeDto = themeServiceClient.getThemeById(message.getThemeId(), securityProps.getIntegrations().getApiKey());

        return new MessageDto(
                message.getId(),
                message.getText(),
                themeDto,
                message.getCreateDateTime(),
                message.getUpdateDateTime(),
                creatorDto
        );
    }

    private UUID getUserId(Authentication authentication) {
        val userData = (JwtUserData) authentication.getPrincipal();
        userServiceClient.checkUserById(userData.getId(), securityProps.getIntegrations().getApiKey());
        return userData.getId();
    }

    public List<FoundElementDto> searchForMessages(String substring) {

        List<FoundElementDto> foundElementDtoList = new ArrayList<>();

        List<MessageEntity> messageEntityList = messageRepository.findAllByTextContainingIgnoreCase(substring);
        for (MessageEntity messageEntity : messageEntityList) {
            UserDto creator = userServiceClient.getUserById(messageEntity.getCreatorId(), securityProps.getIntegrations().getApiKey());
            foundElementDtoList.add(
                    new FoundElementDto(
                            messageEntity.getId(),
                            FoundElementType.MESSAGE,
                            messageEntity.getText(),
                            messageEntity.getCreateDateTime(),
                            creator
                    )
            );
        }
        return foundElementDtoList;
    }
}
