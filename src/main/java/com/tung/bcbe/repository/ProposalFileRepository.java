package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProposalFile;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ProposalFileRepository extends PagingAndSortingRepository<ProposalFile, UUID> {
    void deleteByName(String fileName);
    List<ProposalFile> findByProposalId(UUID proposalId);
}
