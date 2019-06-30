package com.tung.bcbe.repository;

import com.tung.bcbe.model.Contractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ContractorRepository extends PagingAndSortingRepository<Contractor, UUID> {
    Page<Contractor> findAllBy(Pageable pageable);
    Page<Contractor> findByAddressNameContains(String name, Pageable pageable);
    Contractor findContractorByEmail(String email);
    void deleteContractorByEmail(String email);
}
