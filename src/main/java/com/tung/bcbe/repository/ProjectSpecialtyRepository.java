package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProjectSpecialty;
import com.tung.bcbe.model.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectSpecialtyRepository extends PagingAndSortingRepository<ProjectSpecialty, UUID> {
    void deleteProjectSpecialtiesByProjectIdAndSpecialtyId(UUID projectId, UUID specialtyId);
    Page<ProjectSpecialty> findBySpecialtyIn(List<Specialty> specialties, Pageable pageable);
}
