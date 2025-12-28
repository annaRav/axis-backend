package com.wiki.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(

    @NotBlank(message = "Message content cannot be blank")
    @Size(max = 5000, message = "Message content must not exceed 5000 characters")
    String content

) {}