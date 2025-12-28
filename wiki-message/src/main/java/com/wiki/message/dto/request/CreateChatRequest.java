package com.wiki.message.dto.request;

import com.wiki.message.document.Chat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateChatRequest(

    @NotNull(message = "Chat type is required")
    Chat.ChatType type,

    String organizationId,  // Required for GROUP chats

    @Size(max = 100, message = "Chat name must not exceed 100 characters")
    String name,  // Required for GROUP chats

    @NotEmpty(message = "Members list cannot be empty")
    @Size(min = 2, message = "Chat must have at least 2 members")
    List<String> members  // List of user UUIDs as Strings

) {}