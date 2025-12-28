package com.wiki.message.mapper;

import com.wiki.message.document.Message;
import com.wiki.message.dto.response.MessageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse toResponse(Message message);
}