package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProjectFile;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectFileRepository extends PagingAndSortingRepository<ProjectFile, UUID> {
    void deleteByName(String fileName);
    List<ProjectFile> findByProjectId(UUID projectId);
}
