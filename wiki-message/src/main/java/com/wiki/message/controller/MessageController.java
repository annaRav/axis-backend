package com.wiki.message.controller;

import com.wiki.common.security.SecurityUtils;
import com.wiki.message.dto.request.SendMessageRequest;
import com.wiki.message.dto.response.MessageResponse;
import com.wiki.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message management endpoints")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Send message", description = "Send a message to a chat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied to chat")
    })
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable String chatId,
            @Valid @RequestBody SendMessageRequest request) {

        UUID senderId = SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        log.info("Sending message to chat {} from user {}", chatId, senderId);
        MessageResponse response = messageService.sendMessage(chatId, request, senderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get chat messages", description = "Get message history for chat with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied to chat")
    })
    public ResponseEntity<Page<MessageResponse>> getChatMessages(
            @PathVariable String chatId,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "50") int size) {

        UUID currentUserId = SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Pageable pageable = PageRequest.of(page, size);
        log.info("Fetching messages for chat {} by user {} (page: {}, size: {})",
                 chatId, currentUserId, page, size);

        Page<MessageResponse> messages = messageService.getChatMessages(chatId, currentUserId, pageable);

        return ResponseEntity.ok(messages);
    }
}