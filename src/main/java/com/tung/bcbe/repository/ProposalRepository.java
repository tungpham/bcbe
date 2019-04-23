package com.tung.bcbe.repository;

import com.tung.bcbe.model.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    Page<Proposal> findByProjectId(UUID projectId, Pageable pageable);

    Page<Proposal> findBySubContractorId(UUID subContractorId, Pageable pageable);
}
