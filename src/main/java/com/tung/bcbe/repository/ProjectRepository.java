package com.tung.bcbe.repository;

import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.ProjectSpecialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {
    Page<Project> findByGenContractorIdAndStatus(UUID genId, Project.Status status, Pageable pageable);
    Page<Project> findAllByStatus(Project.Status status, Pageable pageable);
    Page<Project> findByGenContractorIdAndType(UUID genId, Project.Type type, Pageable pageable);
//    Page<Project> findProjectsByProjectSpecialties(UUID[] uuids, Pageable pageable);
}
