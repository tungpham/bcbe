package com.tung.bcbe.repository;

import com.tung.bcbe.model.GenContractor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "gencontractors", path = "gencontractors")
public interface GenContractorRepository extends PagingAndSortingRepository<GenContractor, UUID> {
}
