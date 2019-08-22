package com.tung.bcbe.repository;

import com.tung.bcbe.model.Node;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface NodeRepository extends PagingAndSortingRepository<Node, UUID> {
    Node findByName(String name);
    List<Node> findNodeByParentIsNull();
}
