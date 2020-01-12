package com.tung.bcbe.repository;

import com.tung.bcbe.model.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProposalRepository extends PagingAndSortingRepository<Proposal, UUID> {

    Page<Proposal> findByProjectId(UUID projectId, Pageable pageable);
    Page<Proposal> findBySubContractorId(UUID contractorId, Pageable pageable);
    Page<Proposal> findBySubContractorIdAndStatus(UUID contractorId, Proposal.STATUS status, Pageable pageable);
    Page<Proposal> findByProjectIdAndStatus(UUID projectId, Proposal.STATUS status, Pageable pageable);
    Page<Proposal> findBySubContractorIdAndStatusAndDescriptionContainsOrProjectTitleContainsOrProjectDescriptionContains(
            UUID contractorId, Proposal.STATUS status, String descTerm, String projTitleTerm, String projDescTerm, Pageable pageable);
}

