package com.tung.bcbe.repository;

import com.tung.bcbe.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {
    Page<Project> findByGenContractorIdAndStatus(UUID genId, Project.Status status, Pageable pageable);
    Page<Project> findAllByStatus(Project.Status status, Pageable pageable);
    Page<Project> findByGenContractorIdAndType(UUID genId, Project.Type type, Pageable pageable);
    Page<Project> findByGenContractorIdAndStatusAndDescriptionContainsOrTitleContains(
            UUID genId, Project.Status status, String descTerm, String titleTerm, Pageable pageable);
}
