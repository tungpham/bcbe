package com.tung.bcbe.repository;

import com.tung.bcbe.model.Template;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "templates", path = "templates")
public interface TemplateRepository extends PagingAndSortingRepository<Template, UUID> {
}
