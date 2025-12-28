package com.wiki.message.repository;

import com.wiki.message.document.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    /**
     * Find messages by chat ID with pagination (sorted by timestamp desc)
     */
    Page<Message> findByChatIdOrderByTimestampDesc(String chatId, Pageable pageable);

    /**
     * Count unread messages in chat
     */
    long countByChatIdAndStatus(String chatId, Message.MessageStatus status);
}