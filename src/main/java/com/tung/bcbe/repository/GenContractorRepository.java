package com.tung.bcbe.repository;

import com.tung.bcbe.model.GenContractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GenContractorRepository extends CrudRepository<GenContractor, UUID> {
    Page<GenContractor> findAllBy(Pageable pageable);
}
