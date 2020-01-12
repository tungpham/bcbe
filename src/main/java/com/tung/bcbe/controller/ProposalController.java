package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.dto.ProposalTemCatOptionDetail;
import com.tung.bcbe.model.Category;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.Proposal;
import com.tung.bcbe.model.ProposalFile;
import com.tung.bcbe.model.ProposalOption;
import com.tung.bcbe.model.Template;
import com.tung.bcbe.repository.CategoryRepository;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.MessageRepository;
import com.tung.bcbe.repository.OptionRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.ProjectTemplateRepository;
import com.tung.bcbe.repository.ProposalFileRepository;
import com.tung.bcbe.repository.ProposalOptionRepository;
import com.tung.bcbe.repository.ProposalRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
public class ProposalController {

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProposalOptionRepository proposalOptionRepository;

    @Autowired
    private ProjectTemplateRepository projectTemplateRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProposalFileRepository proposalFileRepository;

    @Autowired
    AmazonS3 s3;

    @Value("${S3_BUCKET_PROPOSAL}")
    private String bucket;

    @PostMapping("/contractors/{sub_id}/projects/{project_id}/proposals")
    public Proposal createProposal(@PathVariable(value = "sub_id") UUID subId,
                                   @PathVariable(value = "project_id") UUID projectId,
                                   @Valid @RequestBody Proposal proposal)
            throws ExecutionException, InterruptedException {

        CompletableFuture<Contractor> subContractor = CompletableFuture.supplyAsync(() ->
                contractorRepository.findById(subId))
                .thenApply(x -> x.orElseThrow(Util.notFound(subId, Contractor.class)));

        CompletableFuture<Project> project = CompletableFuture.supplyAsync(() ->
                projectRepository.findById(projectId))
                .thenApply(x -> x.orElseThrow(Util.notFound(projectId, Project.class)));

        return subContractor.thenCombine(project, (sub, proj) -> {
            proposal.setSubContractor(sub);
            proposal.setProject(proj);
            proposal.setStatus(Proposal.STATUS.SUBMITTED);
            return proposalRepository.save(proposal);
        }).get();
    }

    @GetMapping("/projects/{project_id}/proposals")
    public Page<Proposal> getProposalsByProjectId(@PathVariable(value = "project_id") UUID projectId,
                                                  @RequestParam(value = "status", required = false) Proposal.STATUS status,
                                                  Pageable pageable) {
        if (status == null) {
            return proposalRepository.findByProjectId(projectId, pageable);
        } else {
            return proposalRepository.findByProjectIdAndStatus(projectId, status, pageable);
        }
    }

    @ApiOperation(value = "Get all proposals submitted by the contractor")
    @GetMapping("/contractors/{sub_id}/proposals")
    public Page<Proposal> getProposalsBySubContractorId(@ApiParam(value = "contractor id") @PathVariable(value = "sub_id") UUID subId,
                                                        @RequestParam(value = "status", required = false) Proposal.STATUS status,
                                                        Pageable pageable) {
        if (status == null)
            return proposalRepository.findBySubContractorId(subId, pageable);
        else
            return proposalRepository.findBySubContractorIdAndStatus(subId, status, pageable);
    }

    @ApiOperation(value = "Get all proposals submitted by the contractor")
    @GetMapping("/contractors/{sub_id}/proposals/search")
    public Page<Proposal> getProposalsBySubContractorId(@ApiParam(value = "contractor id") @PathVariable(value = "sub_id") UUID subId,
                                                        @RequestParam(value = "status") Proposal.STATUS status,
                                                        @RequestParam(value = "term") String term,
                                                        Pageable pageable) {
        return proposalRepository.findBySubContractorIdAndStatusAndDescriptionContainsOrProjectTitleContainsOrProjectDescriptionContains(
                subId, status, term, term, term, pageable);
    }

    @GetMapping("/proposals/{proposal_id}")
    public Proposal getProposal(@PathVariable(value = "proposal_id") UUID proposalId) {
        return proposalRepository.findById(proposalId).orElseThrow(Util.notFound(proposalId, Proposal.class));
    }

    @PutMapping("/proposals/{proposal_id}")
    public Proposal editProposal(@PathVariable(value = "proposal_id") UUID proposalId,
                                 @RequestBody @Valid Proposal proposal) {
        return proposalRepository.findById(proposalId).map(current -> {
            if (proposal.getDescription() != null) {
                current.setDescription(proposal.getDescription());
            }
            if (proposal.getBudget() != null) {
                current.setBudget(proposal.getBudget());
            }
            if (proposal.getStatus() != null) {
                current.setStatus(proposal.getStatus());
            }
            if (proposal.getDuration() != null) {
                current.setDuration(proposal.getDuration());
            }
            return proposalRepository.save(current);
        }).orElseThrow(Util.notFound(proposalId, Proposal.class));
    }

    @Transactional
    @DeleteMapping("/proposals/{proposal_id}")
    public void deleteProposal(@PathVariable(value = "proposal_id") UUID proposalId) {
        messageRepository.deleteByProposalId(proposalId);
        proposalRepository.deleteById(proposalId);
    }

    @PostMapping("/proposals/{prop_id}/categories/{cat_id}/options")
    public ProposalOption addProposalOption(@PathVariable(value = "prop_id") UUID propId,
                                            @PathVariable(value = "cat_id") UUID catId,
                                            @RequestBody @Valid ProposalOption proposalOption) {
        return proposalRepository.findById(propId).map(proposal ->
            categoryRepository.findById(catId).map(category -> {
                proposalOption.setCategory(category);
                proposalOption.setProposal(proposal);
                return proposalOptionRepository.save(proposalOption);
            }).orElseThrow(Util.notFound(catId, Category.class))
        ).orElseThrow(Util.notFound(propId, Proposal.class));
    }

    @GetMapping("/proposals/{prop_id}/categories/{cat_id}")
    public Page<ProposalOption> getProposalOptionByCategory(@PathVariable(value = "prop_id") UUID propId,
                                                  @PathVariable(value = "cat_id") UUID catId, Pageable pageable) {
        return proposalOptionRepository.findByProposalIdAndCategoryId(propId, catId, pageable);
    }

    @GetMapping("/proposals/options/{opt_id}")
    public ProposalOption getProposalOption(@PathVariable(value = "opt_id") UUID optId) {
        return proposalOptionRepository.findById(optId).orElseThrow(Util.notFound(optId, ProposalOption.class));
    }

    @PutMapping("/proposals/options/{opt_id}")
    public ProposalOption editProposalOption(@PathVariable(value = "opt_id") UUID optId,
                                   @RequestBody @Valid ProposalOption proposalOption) {
        return proposalOptionRepository.findById(optId).map(current -> {
            if (proposalOption.getBudget() != null) {
                current.setBudget(proposalOption.getBudget());
            }
            if (proposalOption.getDuration() != null) {
                current.setDuration(proposalOption.getDuration());
            }
            if (proposalOption.getDescription() != null) {
                current.setDescription(proposalOption.getDescription());
            }
            if (proposalOption.getName() != null) {
                current.setName(proposalOption.getName());
            }
            if (proposalOption.getValue() != null) {
                current.setValue(proposalOption.getValue());
            }
            return proposalOptionRepository.save(current);
        }).orElseThrow(Util.notFound(optId, ProposalOption.class));
    }

    @DeleteMapping("/proposals/options/{opt_id}")
    public void deleteProposalOption(@PathVariable(value = "opt_id") UUID optId) {
        proposalOptionRepository.deleteById(optId);
    }

    @PostMapping("/proposals/{proposal_id}/files/upload")
    public void uploadFile(@PathVariable(value = "proposal_id") UUID proposalId,
                           @RequestParam("file") MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        upload(proposalId, file);
    }

    @PostMapping("/proposals/{proposal_id}/files/upload/multiple")
    public void uploadFile(@PathVariable(value = "proposal_id") UUID proposalId,
                           @RequestParam("file") MultipartFile[] files,
                           RedirectAttributes redirectAttributes) {
        for (MultipartFile file : files) {
            upload(proposalId, file);
        }
    }

    public void upload(UUID proposalId, MultipartFile file) {
        proposalRepository.findById(proposalId).ifPresent(proposal -> {
            try {
                ProposalFile proposalFile = new ProposalFile();
                proposalFile.setName(file.getOriginalFilename());
                proposalFile.setProposal(proposal);

                String key = proposal.getId() + "/" + file.getOriginalFilename();
                Util.putFile(s3, bucket, key, file);
                proposalFileRepository.save(proposalFile);

            } catch (IOException e) {
                log.error("error uploading file", e);
                throw new RuntimeException(e);
            }
        });
    }

    @GetMapping("/proposals/{proposal_id}/files")
    public List<ProposalFile> getProposalFiles(@PathVariable(value = "proposal_id") UUID proposalId) {
        return proposalFileRepository.findByProposalId(proposalId);
    }

    @Transactional
    @DeleteMapping("/proposals/{proposal_id}/files/{file_name}")
    public void deleteFile(@PathVariable(value = "proposal_id") UUID proposalId,
                           @PathVariable(value = "file_name") String fileName) {
        proposalRepository.findById(proposalId).ifPresent(proposal -> {
            String key = proposal.getId() + "/" + fileName;
            s3.deleteObject(bucket, key);
            proposalFileRepository.deleteByName(fileName);
        });
    }

    @GetMapping("/proposals/{proposal_id}/files/{file_name}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "proposal_id") UUID proposalId,
                                           @PathVariable(value = "file_name") String fileName) throws IOException {
        String key = proposalId + "/" + fileName;
        return Util.download(s3, bucket, key);
    }

    @GetMapping("/proposals/{prop_id}/temCatOptionDetail")
    public ProposalTemCatOptionDetail getProposalOptions(@PathVariable(value = "prop_id") UUID propId) {
        return proposalRepository.findById(propId).map(proposal -> {
            ProposalTemCatOptionDetail proposalTemCatOptionDetail = new ProposalTemCatOptionDetail();
            proposalTemCatOptionDetail.setProposal(proposal);
            Project project  = proposal.getProject();
            log.info("project is " + project);
            project.getProjectTemplates().forEach(projectTemplate -> {
                Template template = projectTemplate.getTemplate();
                Map<UUID, List<Map<UUID, List<ProposalOption>>>> temCatsMap = new HashMap<>();
                temCatsMap.put(template.getId(), new ArrayList<>());
                template.getCategoryList().forEach(category -> {
                    List<ProposalOption> options = proposalOptionRepository.findByProposalIdAndCategoryId(propId, category.getId());
                    Map<UUID, List<ProposalOption>> catOptionsMap = new HashMap<>();
                    catOptionsMap.put(category.getId(), options);
                    temCatsMap.get(template.getId()).add(catOptionsMap);
                });
                proposalTemCatOptionDetail.getTemCatOptionDetail().add(temCatsMap);
            });
            return proposalTemCatOptionDetail;
        }).orElseThrow(Util.notFound(propId, Proposal.class));
    }
}
