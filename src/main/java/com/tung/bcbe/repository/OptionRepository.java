package com.tung.bcbe.repository;

import com.tung.bcbe.model.Option;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface OptionRepository extends PagingAndSortingRepository<Option, UUID> {
}
