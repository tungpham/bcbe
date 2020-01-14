package com.tung.bcbe.repository;

import com.tung.bcbe.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {
    Page<Project> findByGenContractorIdAndStatus(UUID genId, Project.Status status, Pageable pageable);
    Page<Project> findAllByStatus(Project.Status status, Pageable pageable);
    Page<Project> findByGenContractorIdAndType(UUID genId, Project.Type type, Pageable pageable);

    @Query("select p from project p, contractor c where p.genContractor.id = c.id and c.id = ?1 and p.status = ?2 and (p.description like %?3 or p.title like %?4)")
    Page<Project> searchProject(UUID genId, Project.Status status, String descTerm, String titleTerm, Pageable pageable);
}
