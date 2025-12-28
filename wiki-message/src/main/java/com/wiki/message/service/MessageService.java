package com.wiki.message.service;

import com.wiki.message.dto.request.SendMessageRequest;
import com.wiki.message.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageService {

    /**
     * Send message to chat
     */
    MessageResponse sendMessage(String chatId, SendMessageRequest request, UUID senderId);

    /**
     * Get message history for chat with pagination
     */
    Page<MessageResponse> getChatMessages(String chatId, UUID currentUserId, Pageable pageable);
}