package com.tung.bcbe.repository;

import com.tung.bcbe.model.Category;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;

@CrossOrigin(origins = "*")
@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, UUID> {
}
