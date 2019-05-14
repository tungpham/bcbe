package com.tung.bcbe.repository;

import com.tung.bcbe.model.Contractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ContractorRepository extends CrudRepository<Contractor, UUID> {
    Page<Contractor> findAllBy(Pageable pageable);
}
