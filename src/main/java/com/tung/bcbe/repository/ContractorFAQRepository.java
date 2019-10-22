package com.tung.bcbe.repository;

import com.tung.bcbe.model.ContractorFAQ;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ContractorFAQRepository extends PagingAndSortingRepository<ContractorFAQ, UUID> {
    public List<ContractorFAQ> findAllById(UUID id, Pageable pageable);
}
