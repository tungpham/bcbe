package com.tung.bcbe.repository;

import com.tung.bcbe.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NodeRepository extends JpaRepository<Node, UUID> {
    Node findByName(String name);
}
