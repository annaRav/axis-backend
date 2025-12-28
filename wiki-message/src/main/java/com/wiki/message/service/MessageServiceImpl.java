package com.wiki.message.service;

import com.wiki.common.exception.BusinessException;
import com.wiki.message.document.Message;
import com.wiki.message.dto.request.SendMessageRequest;
import com.wiki.message.dto.response.MessageResponse;
import com.wiki.message.mapper.MessageMapper;
import com.wiki.message.repository.ChatRepository;
import com.wiki.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;
    private final ChatService chatService;

    @Override
    public MessageResponse sendMessage(String chatId, SendMessageRequest request, UUID senderId) {
        log.debug("Sending message to chat {} from user {}", chatId, senderId);

        // Check if user has access to chat
        if (!chatService.hasAccessToChat(chatId, senderId)) {
            throw new BusinessException("Access denied to chat", HttpStatus.FORBIDDEN);
        }

        Message message = Message.builder()
                .chatId(chatId)
                .senderId(senderId.toString())
                .content(request.content())
                .status(Message.MessageStatus.SENT)
                .build();

        message.generateIdIfNeeded();

        Message savedMessage = messageRepository.save(message);

        // Update chat's lastMessageAt
        chatRepository.findById(chatId).ifPresent(chat -> {
            chat.setLastMessageAt(LocalDateTime.now());
            chatRepository.save(chat);
        });

        log.info("Message {} sent to chat {}", savedMessage.getId(), chatId);

        return messageMapper.toResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getChatMessages(String chatId, UUID currentUserId, Pageable pageable) {
        log.debug("Fetching messages for chat {} by user {}", chatId, currentUserId);

        // Check if user has access to chat
        if (!chatService.hasAccessToChat(chatId, currentUserId)) {
            throw new BusinessException("Access denied to chat", HttpStatus.FORBIDDEN);
        }

        Page<Message> messages = messageRepository.findByChatIdOrderByTimestampDesc(chatId, pageable);

        return messages.map(messageMapper::toResponse);
    }
}