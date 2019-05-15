package com.tung.bcbe.repository;

import com.tung.bcbe.model.Address;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, UUID> {
}
