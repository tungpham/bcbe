package com.tung.bcbe.repository;

import com.tung.bcbe.model.ContractorFile;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ContractorFileRepository extends PagingAndSortingRepository<ContractorFile, UUID> {
}
