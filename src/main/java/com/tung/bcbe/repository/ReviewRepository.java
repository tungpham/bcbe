package com.tung.bcbe.repository;

import com.tung.bcbe.model.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends PagingAndSortingRepository<Review, UUID> {
    List<Review> findAllById(UUID id, Pageable pageable);
}
