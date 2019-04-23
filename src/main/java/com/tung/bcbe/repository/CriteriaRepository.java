package com.tung.bcbe.repository;

import com.tung.bcbe.model.Criteria;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "category", path = "category")
public interface CriteriaRepository extends PagingAndSortingRepository<Criteria, UUID> {
}
