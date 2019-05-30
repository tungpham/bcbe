package com.tung.bcbe.repository;

import com.tung.bcbe.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface MessageRepository extends PagingAndSortingRepository<Message, UUID> {
    Page<Message> findByProposalIdOrderByCreatedAtDesc(UUID propId, Pageable pageable);
}
