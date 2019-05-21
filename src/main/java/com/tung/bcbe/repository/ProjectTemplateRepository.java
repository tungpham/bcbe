package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProjectTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProjectTemplateRepository extends PagingAndSortingRepository<ProjectTemplate, UUID> {
    void deleteProjectTemplateByProjectIdAndTemplateId(UUID projectId, UUID templateId);
}
