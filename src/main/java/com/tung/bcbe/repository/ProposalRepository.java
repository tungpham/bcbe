package com.tung.bcbe.repository;

import com.tung.bcbe.model.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProposalRepository extends PagingAndSortingRepository<Proposal, UUID> {

    Page<Proposal> findByProjectId(UUID projectId, Pageable pageable);
    Page<Proposal> findBySubContractorId(UUID contractorId, Pageable pageable);
    Page<Proposal> findBySubContractorIdAndStatus(UUID contractorId, Proposal.STATUS status, Pageable pageable);
    Page<Proposal> findByProjectIdAndStatus(UUID projectId, Proposal.STATUS status, Pageable pageable);

    @Query("select p from proposal p, contractor c where p.subContractor.id = c.id and c.id = ?1 and p.status = ?2 and " +
            "(p.description like %?3 or p.project.title like %?3 or p.project.description like %?3)")
    Page<Proposal> searchProposal(UUID contractorId, Proposal.STATUS status, String term, Pageable pageable);
}
