package com.tung.bcbe.repository;

import com.tung.bcbe.model.Template;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "templates", path = "templates")
public interface TemplateRepository extends PagingAndSortingRepository<Template, UUID> {
}
