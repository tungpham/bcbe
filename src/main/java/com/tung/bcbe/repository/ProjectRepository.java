package com.tung.bcbe.repository;

import com.tung.bcbe.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {
    Page<Project> findByContractorId(UUID genId, Pageable pageable);
}
