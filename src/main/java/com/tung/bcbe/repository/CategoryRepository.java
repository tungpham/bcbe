package com.tung.bcbe.repository;

import com.tung.bcbe.model.Category;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CategoryRepository extends PagingAndSortingRepository<Category, UUID> {
}
