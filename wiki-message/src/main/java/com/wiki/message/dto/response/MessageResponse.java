package com.wiki.message.dto.response;

import com.wiki.message.document.Message;

import java.time.LocalDateTime;

public record MessageResponse(
    String id,
    String chatId,
    String senderId,
    String content,
    LocalDateTime timestamp,
    Message.MessageStatus status
) {}