package com.tung.bcbe.controller;

import com.tung.bcbe.model.Project;
import com.tung.bcbe.repository.GenContractorRepository;
import com.tung.bcbe.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GenContractorRepository genContractorRepository;

    @PostMapping("/gencontractors/{gen_id}/projects")
    public Project createProject(@PathVariable(value = "gen_id") UUID genId,
                                 @Valid @RequestBody Project project) {
        return genContractorRepository.findById(genId).map(genContractor -> {
            project.setGenContractor(genContractor);
            return projectRepository.save(project);
        }).orElseThrow(() -> new ResourceNotFoundException("gencontractor_id " + genId + " not found"));
    }

    @GetMapping("/gencontractors/{gen_id}/projects")
    public Page<Project> getProjectsByGenContractor(@PathVariable(value = "gen_id") UUID genId, Pageable pageable) {
        return projectRepository.findByGenContractorId(genId, pageable);
    }
}
