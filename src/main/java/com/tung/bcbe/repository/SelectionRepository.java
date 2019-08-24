package com.tung.bcbe.repository;

import com.tung.bcbe.model.Selection;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface SelectionRepository extends PagingAndSortingRepository<Selection, UUID> {
}
