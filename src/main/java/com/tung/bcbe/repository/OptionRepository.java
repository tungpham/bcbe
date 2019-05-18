package com.tung.bcbe.repository;

import com.tung.bcbe.model.Option;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;

@CrossOrigin(allowedHeaders = "*")
public interface OptionRepository extends PagingAndSortingRepository<Option, UUID> {
}
