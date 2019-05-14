package com.tung.bcbe.repository;

import com.tung.bcbe.model.SubContractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SubContractorRepository extends CrudRepository<SubContractor, UUID> {
    Page<SubContractor> findAllBy(Pageable pageable);
}
