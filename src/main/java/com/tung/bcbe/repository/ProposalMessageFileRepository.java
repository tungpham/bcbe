package com.tung.bcbe.repository;

import com.tung.bcbe.model.ProposalMessageFile;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProposalMessageFileRepository extends PagingAndSortingRepository<ProposalMessageFile, UUID> {
}
