package com.wiki.message.controller;

import com.wiki.common.security.SecurityUtils;
import com.wiki.message.dto.request.CreateChatRequest;
import com.wiki.message.dto.response.ChatResponse;
import com.wiki.message.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat management endpoints")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Create new chat", description = "Create a new private or group chat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Chat created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "409", description = "Chat already exists")
    })
    public ResponseEntity<ChatResponse> createChat(@Valid @RequestBody CreateChatRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        log.info("Creating chat by user {}", currentUserId);
        ChatResponse response = chatService.createChat(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get user chats", description = "Get all chats for current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chats retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ChatResponse>> getUserChats() {
        UUID currentUserId = SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        log.info("Fetching chats for user {}", currentUserId);
        List<ChatResponse> chats = chatService.getUserChats(currentUserId);

        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}")
    @Operation(summary = "Get chat by ID", description = "Get chat details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chat retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Chat not found")
    })
    public ResponseEntity<ChatResponse> getChatById(@PathVariable String chatId) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        log.info("Fetching chat {} for user {}", chatId, currentUserId);
        ChatResponse chat = chatService.getChatById(chatId, currentUserId);

        return ResponseEntity.ok(chat);
    }
}