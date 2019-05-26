package com.tung.bcbe.repository;

import com.tung.bcbe.model.ID;
import com.tung.bcbe.model.ProposalOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ProposalOptionRepository extends PagingAndSortingRepository<ProposalOption, ID> {
    Page<ProposalOption> findByProposalIdAndCategoryId(UUID propId, UUID catId, Pageable pageable);
    List<ProposalOption> findByProposalIdAndCategoryId(UUID propId, UUID catId);
}
