package com.tung.bcbe.repository;

import com.tung.bcbe.model.ContractorSpecialty;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ContractorSpecialtyRepository extends PagingAndSortingRepository<ContractorSpecialty, UUID> {
    void deleteContractorSpecialtiesByContractorIdAndSpecialtyId(UUID contractorId, UUID specialtyId);
}
