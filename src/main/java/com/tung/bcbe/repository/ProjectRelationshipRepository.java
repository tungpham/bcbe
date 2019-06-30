package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProjectRelationship;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProjectRelationshipRepository extends PagingAndSortingRepository<ProjectRelationship, UUID> {
}
