package com.tung.bcbe.repository;

import com.tung.bcbe.model.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProposalRepository extends PagingAndSortingRepository<Proposal, UUID> {

    Page<Proposal> findByProjectId(UUID projectId, Pageable pageable);

    Page<Proposal> findByContractorId(UUID contractorId, Pageable pageable);
}
