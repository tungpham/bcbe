package com.tung.bcbe.repository;

import com.tung.bcbe.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "options", path = "options")
public interface OptionRepository extends JpaRepository<Option, UUID> {
}
