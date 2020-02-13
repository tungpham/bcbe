package com.tung.bcbe.repository;

import com.tung.bcbe.model.Message2;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface Message2Repository extends PagingAndSortingRepository<Message2, UUID> {
}
