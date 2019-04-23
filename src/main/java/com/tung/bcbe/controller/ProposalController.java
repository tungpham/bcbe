package com.tung.bcbe.controller;

import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.Proposal;
import com.tung.bcbe.model.SubContractor;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.ProposalRepository;
import com.tung.bcbe.repository.SubContractorRepository;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class ProposalController {

    @Autowired
    private SubContractorRepository subContractorRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @PostMapping("/subcontractors/{sub_id}/projects/{project_id}/proposals")
    public Proposal createProposal(@PathVariable(value = "sub_id") UUID subId,
                                   @PathVariable(value = "project_id") UUID projectId,
                                   @Valid @RequestBody Proposal proposal)
            throws ExecutionException, InterruptedException {

        CompletableFuture<SubContractor> subContractor = CompletableFuture.supplyAsync(() ->
                subContractorRepository.findById(subId))
                .thenApply(x -> x.orElseThrow(() -> new ResourceNotFoundException("subcontractor not found " + subId)));

        CompletableFuture<Project> project = CompletableFuture.supplyAsync(() ->
                projectRepository.findById(projectId))
                .thenApply(x -> x.orElseThrow(() -> new ResourceNotFoundException("project not found " + projectId)));

        return subContractor.thenCombine(project, (sub, proj) -> {
            proposal.setSubContractor(sub);
            proposal.setProject(proj);
            return proposalRepository.save(proposal);
        }).get();
    }

    @GetMapping("/projects/{project_id}/proposals")
    public Page<Proposal> getProposalsByProjectId(@PathVariable(value = "project_id") UUID projectId, Pageable pageable) {
        return proposalRepository.findByProjectId(projectId, pageable);
    }

    @GetMapping("/subcontractors/{sub_id}/proposals")
    public Page<Proposal> getProposalsBySubContractorId(@PathVariable(value = "sub_id") UUID subId, Pageable pageable) {
        return proposalRepository.findBySubContractorId(subId, pageable);
    }
}
