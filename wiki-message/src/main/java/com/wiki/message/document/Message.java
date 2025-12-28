package com.wiki.message.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "messages")
@CompoundIndexes({
    @CompoundIndex(name = "idx_chat_timestamp", def = "{'chatId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_chat_status", def = "{'chatId': 1, 'status': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    private String id;

    @Indexed
    private String chatId;

    @Indexed
    private String senderId;  // UUID as String

    private String content;

    @CreatedDate
    private LocalDateTime timestamp;

    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }

    // Helper method to generate UUID if not set
    public void generateIdIfNeeded() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}