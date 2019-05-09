package com.tung.bcbe.repository;

import com.tung.bcbe.model.GenContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "gencontractors", path = "gencontractors")
public interface GenContractorRepository extends JpaRepository<GenContractor, UUID> {
}
