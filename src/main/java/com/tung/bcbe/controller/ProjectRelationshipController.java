package com.tung.bcbe.controller;

import com.tung.bcbe.repository.ProjectRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class ProjectRelationshipController {
    
    @Autowired
    private ProjectRelationshipRepository projectRelationshipRepository;
}
