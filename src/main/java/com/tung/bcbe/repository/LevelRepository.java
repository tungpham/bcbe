package com.tung.bcbe.repository;

import com.tung.bcbe.model.Level;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface LevelRepository extends PagingAndSortingRepository<Level, UUID> {
    List<Level> findByProjectId(UUID projectId);
}
