package com.tung.bcbe.repository;

import com.tung.bcbe.model.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ConversationRepository extends PagingAndSortingRepository<Conversation, UUID> {
    Page<Conversation> findByProjectId(UUID projectId, Pageable pageable);

    Page<Conversation> findByContractorId(UUID conId, Pageable pageable);

    Conversation findByProjectIdAndContractorId(UUID projectId, UUID conId);

    Conversation findByProjectIdAndProjectGenContractorId(UUID projectId, UUID genId);
}
