package com.tung.bcbe.repository;

import com.tung.bcbe.model.Contractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractorRepository extends PagingAndSortingRepository<Contractor, UUID> {
    Page<Contractor> findAllBy(Pageable pageable);
}
