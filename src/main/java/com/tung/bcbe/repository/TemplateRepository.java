package com.tung.bcbe.repository;

import com.tung.bcbe.model.Template;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TemplateRepository extends PagingAndSortingRepository<Template, UUID> {
}
