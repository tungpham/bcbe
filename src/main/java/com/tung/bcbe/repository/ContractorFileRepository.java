package com.tung.bcbe.repository;

import com.tung.bcbe.model.ContractorFile;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ContractorFileRepository extends PagingAndSortingRepository<ContractorFile, UUID> {

    List<ContractorFile> findContractorFileByContractorIdAndType(UUID conId, ContractorFile.Type type);

    List<ContractorFile> findContractorFileByContractorIdAndTypeIn(UUID conId, List<ContractorFile.Type> types);
}
