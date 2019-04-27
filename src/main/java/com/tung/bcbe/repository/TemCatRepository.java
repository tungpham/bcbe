package com.tung.bcbe.repository;

import com.tung.bcbe.model.TemCat;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "temcats", path = "temcats")
public interface TemCatRepository extends PagingAndSortingRepository<TemCat, UUID> {

    List<TemCat> findCategoriesByTemplateId(UUID temId);
}
