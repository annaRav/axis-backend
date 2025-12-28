package com.wiki.message.repository;

import com.wiki.message.document.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    /**
     * Find all chats where user is a member
     */
    @Query("{ 'members': ?0 }")
    List<Chat> findAllByMembersContaining(String userId);

    /**
     * Find private chat between two specific users
     */
    @Query("{ 'type': 'PRIVATE', 'members': { $all: [?0, ?1], $size: 2 } }")
    Optional<Chat> findPrivateChatBetweenUsers(String userId1, String userId2);

    /**
     * Find all group chats in organization
     */
    @Query("{ 'organizationId': ?0, 'type': 'GROUP' }")
    List<Chat> findGroupChatsByOrganization(String organizationId);

    /**
     * Check if user is member of chat
     */
    @Query("{ '_id': ?0, 'members': ?1 }")
    Optional<Chat> findByIdAndMembersContaining(String chatId, String userId);
}