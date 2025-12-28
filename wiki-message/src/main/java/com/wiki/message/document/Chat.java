package com.wiki.message.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "chats")
@CompoundIndexes({
    @CompoundIndex(name = "idx_org_type", def = "{'organizationId': 1, 'type': 1}"),
    @CompoundIndex(name = "idx_members", def = "{'members': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    private String id;

    @Indexed
    private String organizationId;  // UUID as String

    private ChatType type;

    private String name;  // For GROUP chats only

    private List<String> members;  // List of user UUIDs as Strings

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    private LocalDateTime lastMessageAt = null;

    public enum ChatType {
        PRIVATE,
        GROUP
    }

    // Helper method to generate UUID if not set
    public void generateIdIfNeeded() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}