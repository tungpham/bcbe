package com.tung.bcbe.repository;

import com.tung.bcbe.model.ConversationMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ConversationMessageRepository extends PagingAndSortingRepository<ConversationMessage, UUID> {

    Page<ConversationMessage> findAllByConversationId(UUID convoId, Pageable pageable);

    ConversationMessage findTopByConversationIdOrderByMessage2UpdatedAt(UUID convoId);
}
