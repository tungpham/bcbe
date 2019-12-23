package com.tung.bcbe.repository;

import com.tung.bcbe.model.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface SpecialtyRepository extends PagingAndSortingRepository<Specialty, UUID> {
    List<Specialty> findAllById(List<UUID> uuids);
}
