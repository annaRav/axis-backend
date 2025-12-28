package com.wiki.message.service;

import com.wiki.common.exception.BusinessException;
import com.wiki.common.exception.ResourceNotFoundException;
import com.wiki.message.document.Chat;
import com.wiki.message.dto.request.CreateChatRequest;
import com.wiki.message.dto.response.ChatResponse;
import com.wiki.message.mapper.ChatMapper;
import com.wiki.message.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public ChatResponse createChat(CreateChatRequest request, UUID currentUserId) {
        log.debug("Creating chat of type {} by user {}", request.type(), currentUserId);

        // Validate members list contains current user
        if (!request.members().contains(currentUserId.toString())) {
            throw new BusinessException("Current user must be in members list", HttpStatus.BAD_REQUEST);
        }

        // Validate chat type constraints
        if (request.type() == Chat.ChatType.PRIVATE) {
            validatePrivateChat(request, currentUserId);
        } else {
            validateGroupChat(request);
        }

        Chat chat = chatMapper.toEntity(request);
        chat.generateIdIfNeeded();

        Chat savedChat = chatRepository.save(chat);
        log.info("Created chat {} of type {}", savedChat.getId(), savedChat.getType());

        return chatMapper.toResponse(savedChat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponse> getUserChats(UUID currentUserId) {
        log.debug("Fetching chats for user {}", currentUserId);

        List<Chat> chats = chatRepository.findAllByMembersContaining(currentUserId.toString());

        return chats.stream()
                .map(chatMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatResponse getChatById(String chatId, UUID currentUserId) {
        log.debug("Fetching chat {} for user {}", chatId, currentUserId);

        Chat chat = chatRepository.findByIdAndMembersContaining(chatId, currentUserId.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found or access denied"));

        return chatMapper.toResponse(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessToChat(String chatId, UUID currentUserId) {
        return chatRepository.findByIdAndMembersContaining(chatId, currentUserId.toString())
                .isPresent();
    }

    private void validatePrivateChat(CreateChatRequest request, UUID currentUserId) {
        // Private chat must have exactly 2 members
        if (request.members().size() != 2) {
            throw new BusinessException("Private chat must have exactly 2 members", HttpStatus.BAD_REQUEST);
        }

        // Check if private chat already exists between these users
        String otherUserId = request.members().stream()
                .filter(id -> !id.equals(currentUserId.toString()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Invalid members list", HttpStatus.BAD_REQUEST));

        chatRepository.findPrivateChatBetweenUsers(currentUserId.toString(), otherUserId)
                .ifPresent(chat -> {
                    throw new BusinessException("Private chat already exists between these users", HttpStatus.CONFLICT);
                });
    }

    private void validateGroupChat(CreateChatRequest request) {
        // Group chat must have at least 2 members
        if (request.members().size() < 2) {
            throw new BusinessException("Group chat must have at least 2 members", HttpStatus.BAD_REQUEST);
        }

        // Group chat must have a name
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException("Group chat must have a name", HttpStatus.BAD_REQUEST);
        }

        // Group chat must have organization ID
        if (request.organizationId() == null) {
            throw new BusinessException("Group chat must belong to an organization", HttpStatus.BAD_REQUEST);
        }
    }
}