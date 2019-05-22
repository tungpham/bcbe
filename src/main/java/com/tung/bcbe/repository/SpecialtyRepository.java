package com.tung.bcbe.repository;

import com.tung.bcbe.model.Specialty;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface SpecialtyRepository extends PagingAndSortingRepository<Specialty, UUID> {
}
