package com.wiki.message.mapper;

import com.wiki.message.document.Chat;
import com.wiki.message.dto.request.CreateChatRequest;
import com.wiki.message.dto.response.ChatResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true)
    Chat toEntity(CreateChatRequest request);

    ChatResponse toResponse(Chat chat);
}