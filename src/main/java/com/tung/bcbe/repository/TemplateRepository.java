package com.tung.bcbe.repository;

import com.tung.bcbe.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "template", path = "template")
public interface TemplateRepository extends JpaRepository<Template, UUID> {
}
