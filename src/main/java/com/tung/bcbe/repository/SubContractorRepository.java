package com.tung.bcbe.repository;

import com.tung.bcbe.model.SubContractor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "subcontractors", path = "subcontractors")
public interface SubContractorRepository extends PagingAndSortingRepository<SubContractor, UUID> {
}
