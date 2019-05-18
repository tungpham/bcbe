package com.tung.bcbe.repository;

import com.tung.bcbe.model.Address;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface AddressRepository extends PagingAndSortingRepository<Address, UUID> {
}
