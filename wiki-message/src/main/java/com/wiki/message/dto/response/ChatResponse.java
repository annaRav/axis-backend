package com.wiki.message.dto.response;

import com.wiki.message.document.Chat;

import java.time.LocalDateTime;
import java.util.List;

public record ChatResponse(
    String id,
    String organizationId,
    Chat.ChatType type,
    String name,
    List<String> members,
    LocalDateTime createdAt,
    LocalDateTime lastMessageAt
) {}