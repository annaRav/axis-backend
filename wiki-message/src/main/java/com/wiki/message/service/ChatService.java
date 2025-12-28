package com.wiki.message.service;

import com.wiki.message.dto.request.CreateChatRequest;
import com.wiki.message.dto.response.ChatResponse;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    /**
     * Create new chat (private or group)
     */
    ChatResponse createChat(CreateChatRequest request, UUID currentUserId);

    /**
     * Get all chats for current user
     */
    List<ChatResponse> getUserChats(UUID currentUserId);

    /**
     * Get chat by ID (with access check)
     */
    ChatResponse getChatById(String chatId, UUID currentUserId);

    /**
     * Check if user has access to chat
     */
    boolean hasAccessToChat(String chatId, UUID currentUserId);
}