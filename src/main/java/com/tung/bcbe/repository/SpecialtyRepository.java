package com.tung.bcbe.repository;

import com.tung.bcbe.model.Specialty;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface SpecialtyRepository extends PagingAndSortingRepository<Specialty, UUID> {
    List<Specialty> findByIdIn(List<UUID> uuids);
    List<Specialty> findAllById(List<UUID> uuids);
}
