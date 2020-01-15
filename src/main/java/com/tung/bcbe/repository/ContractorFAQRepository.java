package com.tung.bcbe.repository;

import com.tung.bcbe.model.ContractorFAQ;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ContractorFAQRepository extends PagingAndSortingRepository<ContractorFAQ, UUID> {
    List<ContractorFAQ> findAllByContractorId(UUID id);
}
