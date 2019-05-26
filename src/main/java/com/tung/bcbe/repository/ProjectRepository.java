package com.tung.bcbe.repository;

import com.tung.bcbe.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {
    Page<Project> findByGenContractorId(UUID genId, Pageable pageable);
}
