package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProjectInvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectInviteRepository extends PagingAndSortingRepository<ProjectInvite, UUID> {
    Page<ProjectInvite> findBySubContractorId(UUID subId, Pageable pageable);
    List<ProjectInvite> findByProjectId(UUID projectId);
}
