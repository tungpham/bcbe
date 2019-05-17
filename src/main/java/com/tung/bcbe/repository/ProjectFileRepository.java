package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProjectFile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectFileRepository extends PagingAndSortingRepository<ProjectFile, UUID> {
    void deleteByName(String fileName);
    List<ProjectFile> findByProjectId(UUID projectId);
}
