package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProjectSpecialty;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProjectSpecialtyRepository extends PagingAndSortingRepository<ProjectSpecialty, UUID> {
    void deleteProjectSpecialtiesByProjectIdAndSpecialtyId(UUID projectId, UUID specialtyId);
}
