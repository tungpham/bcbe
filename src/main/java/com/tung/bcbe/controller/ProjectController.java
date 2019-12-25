package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.dto.PastProject;
import com.tung.bcbe.dto.ProjectDTO;
import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.ProjectFile;
import com.tung.bcbe.model.ProjectInvite;
import com.tung.bcbe.model.ProjectRelationship;
import com.tung.bcbe.model.ProjectSpecialty;
import com.tung.bcbe.model.ProjectTemplate;
import com.tung.bcbe.model.Proposal;
import com.tung.bcbe.model.Specialty;
import com.tung.bcbe.model.Template;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.ProjectFileRepository;
import com.tung.bcbe.repository.ProjectInviteRepository;
import com.tung.bcbe.repository.ProjectRelationshipRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.ProjectSpecialtyRepository;
import com.tung.bcbe.repository.ProjectTemplateRepository;
import com.tung.bcbe.repository.ProposalRepository;
import com.tung.bcbe.repository.SpecialtyRepository;
import com.tung.bcbe.repository.TemplateRepository;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ProjectFileRepository projectFileRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private ProjectTemplateRepository projectTemplateRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private ProjectSpecialtyRepository projectSpecialtyRepository;

    @Autowired
    private ProjectInviteRepository projectInviteRepository;

    @Autowired
    private ProjectRelationshipRepository projectRelationshipRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private AmazonS3 s3;

    @Value("${S3_BUCKET}")
    private String bucket;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * User create new project. The project will have status ACTIVE and type of OWNER_PROJECT
     * @param genId
     * @param project
     * @return
     */
    @ApiOperation(value = "Create a new project for an owner")
    @PostMapping("/contractors/{gen_id}/projects")
    public Project createProject(@PathVariable(value = "gen_id") UUID genId,
                                 @Valid @RequestBody Project project) {
        return createProject(genId, project, Project.Status.ACTIVE, Project.Type.OWNER_PROJECT);
    }

    /**
     * Sub contractor can manually create past projects for their profile. These projects would have type of
     * SUBCON_PROJECT and status of ARCHIVED
     * @param genId
     * @param pastProject
     * @return
     */
    @ApiOperation(value = "Create a past project for a contractor. It's used when contractor add past project in their profile")
    @PostMapping("/contractors/{gen_id}/projects/past")
    public Project createPastProject(@PathVariable(value = "gen_id") UUID genId,
                                     @Valid @RequestBody PastProject pastProject) {
        Project proj = createProject(genId, pastProject.getProject(), Project.Status.ARCHIVED, Project.Type.SUBCON_PROJECT);
        addSpecialtyToProject(proj.getId(), UUID.fromString(pastProject.getSpecialtyId()));

        proj.setGenContractor(null);
        return proj;
    }

    /**
     * Get past projects for sub contractor
     * @param genId
     * @return
     */
    @ApiOperation(value = "Get all contractor past projects")
    @GetMapping("/contractors/{gen_id}/projects/past")
    public Page<Project> getPastProjects(@PathVariable(value = "gen_id") UUID genId, Pageable pageable) {
        Page<Project> page = projectRepository.findByGenContractorIdAndType(genId, Project.Type.SUBCON_PROJECT, pageable);
        page.forEach(p -> p.setGenContractor(null));
        return page;
    }

    public Project createProject(UUID genId, Project project, Project.Status status, Project.Type type) {
        Contractor con = contractorRepository.findById(genId).get();
        return contractorRepository.findById(genId).map(genContractor -> {
            project.setGenContractor(genContractor);
            project.setStatus(status);
            project.setType(type);
            return projectRepository.save(project);
        }).orElseThrow(Util.notFound(genId, Contractor.class));
    }

    @PostMapping("/contractors/{gen_id}/projects/{proj_id}/child")
    public Project createSubProject(@PathVariable(value = "gen_id") UUID genId,
                                    @PathVariable(value = "proj_id") UUID projId,
                                    @Valid @RequestBody Project project) {
        Project subProject = createProject(genId, project);
        projectRepository.findById(projId).map(parent -> {
            ProjectRelationship relationship = new ProjectRelationship();
            relationship.setParent(parent);
            relationship.setChild(subProject);
            return projectRelationshipRepository.save(relationship);
        }).orElseThrow(Util.notFound(projId, Project.class));
        return subProject;
    }

    /**
     * Get list of current owner projects
     * @param genId
     * @param status
     * @param pageable
     * @return
     */
    @GetMapping("/contractors/{gen_id}/projects")
    public Page<ProjectDTO> getProjectsByGenContractor(
            @PathVariable(value = "gen_id") UUID genId,
            @RequestParam(name = "status", defaultValue = "ACTIVE") Project.Status status,
            Pageable pageable) {
        Page<Project> page = projectRepository.findByGenContractorIdAndStatus(genId, status, pageable);
        List<Project> projects = page.getContent();
        List<ProjectDTO> newProjects = projects.stream()
                .map(project -> {
                    project.setGenContractor(null); // clear out owner info
                    ProjectDTO dto = ProjectDTO.builder().build();
                    switch (status) {
                        case ACTIVE:
                            Page<Proposal> proposals = proposalRepository.findByProjectId(project.getId(), null);
                            dto.setNumberOfBids(proposals.getTotalPages());
                            break;
                        case ONGOING:
                            dto.setContractor(Contractor.builder().address(Address.builder().name("Some contractor").build()).build());
                            break;
                        case ARCHIVED:
                            dto.setContractor(Contractor.builder().address(Address.builder().name("Some contractor").build()).build());
                            break;
                    }
                    project.setSubmittedDate(Util.dateFormat.format(project.getCreatedAt()));
                    dto.setProject(project);
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(newProjects, pageable, page.getTotalElements());
    }

    @ApiOperation(value = "get available jobs based on contractor specialties")
    @GetMapping("/projects/available")
    public Page<ProjectDTO> getJobs(@RequestParam(required = false) UUID[] specialty, Pageable pageable) {
        Page<Project> page;
        if (specialty != null && specialty.length > 0) {
            List<Specialty> sp = specialtyRepository.findByIdIn(Arrays.asList(specialty));
            Page<ProjectSpecialty> ps = projectSpecialtyRepository.findBySpecialtyIn(sp, pageable);
            page = new PageImpl<>(ps.getContent().stream().map(ProjectSpecialty::getProject).collect(Collectors.toList()),
                    pageable, ps.getTotalElements());
        } else {
            page = projectRepository.findAllByStatus(Project.Status.ACTIVE, pageable);
        }

        List<Project> projects = page.getContent();
        List<ProjectDTO> jobs = projects.stream().map(project -> {
            Address address = project.getGenContractor().getAddress();
            if (address == null) {
                address = Address.builder().build();
            }
            if (StringUtils.isEmpty(address.getName())) {
                address.setName("Some Owner");
            }
            project.setGenContractor(Contractor.builder().address(address).build()); //clear out owner info except for address
            project.setSubmittedDate(Util.dateFormat.format(project.getCreatedAt()));

            //TODO remove this fake data
            if (specialty == null) {
                Set<ProjectSpecialty> specialties = new HashSet<>();
                ProjectSpecialty ps1 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Home Design").build()).build();
                ps1.setId(UUID.randomUUID());
                specialties.add(ps1);
                ProjectSpecialty ps2 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Roofing").build()).build();
                ps2.setId(UUID.randomUUID());
                specialties.add(ps2);
                ProjectSpecialty ps3 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Carpet Cleaning Service").build()).build();
                ps3.setId(UUID.randomUUID());
                specialties.add(ps3);
                ProjectSpecialty ps4 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Plumbing & Sewage").build()).build();
                ps4.setId(UUID.randomUUID());
                specialties.add(ps4);
                ProjectSpecialty ps5 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Design & Creative").build()).build();
                ps5.setId(UUID.randomUUID());
                specialties.add(ps5);
                ProjectSpecialty ps6 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Engineering & Architecture").build()).build();
                ps6.setId(UUID.randomUUID());
                specialties.add(ps6);
                ProjectSpecialty ps7 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Restaurant Remodel").build()).build();
                ps7.setId(UUID.randomUUID());
                specialties.add(ps7);
                ProjectSpecialty ps8 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Lighting Design").build()).build();
                ps8.setId(UUID.randomUUID());
                specialties.add(ps8);
                ProjectSpecialty ps9 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Landscape Contractors").build()).build();
                ps9.setId(UUID.randomUUID());
                specialties.add(ps9);
                ProjectSpecialty ps10 = ProjectSpecialty.builder().specialty(Specialty.builder().name("Fencing Contractor").build()).build();
                ps10.setId(UUID.randomUUID());
                specialties.add(ps10);

                project.setProjectSpecialties(specialties);
            }

            ProjectDTO dto = ProjectDTO.builder().build();
            dto.setProject(project);

            Page<Proposal> proposals = proposalRepository.findByProjectId(project.getId(), null);
            dto.setNumberOfBids((int) proposals.getTotalElements());

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(jobs, pageable, page.getTotalElements());
    }

    @GetMapping("/projects/{project_id}")
    public Project getProjectById(@PathVariable(value = "project_id") UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow(Util.notFound(projectId, Project.class));
    }

    @GetMapping("/projects")
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAllByStatus(Project.Status.ACTIVE, pageable);
    }

    @PutMapping("/projects/{project_id}")
    public Project editProjectById(@PathVariable(value = "project_id") UUID projectId,
                                            @RequestBody @Valid Project project) {
        return projectRepository.findById(projectId).map(prj -> {
            if (project.getTitle() != null) {
                prj.setTitle(project.getTitle());
            }
            if (project.getDescription() != null) {
                prj.setDescription(project.getDescription());
            }
            if (project.getBudget() != null) {
                prj.setBudget(project.getBudget());
            }
            if (project.getDue() != null) {
                prj.setDue(project.getDue());
            }
            return projectRepository.save(prj);
        }).orElseThrow(Util.notFound(projectId, Project.class));
    }

    @PutMapping("/projects/{project_id}/archive")
    public void archiveProjectById(@PathVariable(value = "project_id") UUID projectId) {
        projectRepository.findById(projectId).ifPresent(project -> {
            project.setStatus(Project.Status.ARCHIVED);
            projectRepository.save(project);
        });
    }

    @DeleteMapping("/projects/{project_id}")
    public void deleteProjectById(@PathVariable(value = "project_id") UUID projectId) {
        projectRepository.deleteById(projectId);
    }

    @PostMapping("/projects/{project_id}/templates/{tem_id}")
    public Project addTemplateToProject(@PathVariable(value = "project_id") UUID projectId,
                                        @PathVariable(value = "tem_id") UUID temId) {
        return projectRepository.findById(projectId).map(project ->
            templateRepository.findById(temId).map(template -> {
                ProjectTemplate projectTemplate = new ProjectTemplate();
                projectTemplate.setProject(project);
                projectTemplate.setTemplate(template);
                projectTemplateRepository.save(projectTemplate);
                return projectRepository.save(project);
        }).orElseThrow(Util.notFound(temId, Template.class))).orElseThrow(Util.notFound(projectId, Project.class));
    }

    @Transactional
    @DeleteMapping("/projects/{project_id}/templates/{tem_id}")
    public void removeTemplateFromProject(@PathVariable(value = "project_id") UUID projectId,
                                          @PathVariable(value = "tem_id") UUID temId) {
        projectTemplateRepository.deleteProjectTemplateByProjectIdAndTemplateId(projectId, temId);
    }

    @PostMapping("/projects/{project_id}/files/upload")
    public void uploadFile(@PathVariable(value = "project_id") UUID projectId,
                                      @RequestParam("file") MultipartFile file,
                                      RedirectAttributes redirectAttributes) {
        upload(projectId, file);
    }

    @PostMapping("/projects/{project_id}/files/upload/multiple")
    public void uploadFile(@PathVariable(value = "project_id") UUID projectId,
                           @RequestParam("file") MultipartFile[] files,
                           RedirectAttributes redirectAttributes) {
        for (MultipartFile file : files) {
            upload(projectId, file);
        }
    }

    public void upload(UUID projectId, MultipartFile file) {
        projectRepository.findById(projectId).ifPresent(project -> {
            try {
                ProjectFile projectFile = new ProjectFile();
                projectFile.setName(file.getOriginalFilename());
                projectFile.setProject(project);

                String key = project.getId() + "/" + file.getOriginalFilename();
                Util.putFile(s3, bucket, key, file);
                projectFileRepository.save(projectFile);

            } catch (IOException e) {
                log.error("error uploading file", e);
                throw new RuntimeException(e);
            }
        });
    }

    @GetMapping("/projects/{project_id}/files")
    public List<ProjectFile> getProjectFiles(@PathVariable(value = "project_id") UUID projectId) {
        return projectFileRepository.findByProjectId(projectId);
    }

    @Transactional
    @DeleteMapping("/projects/{project_id}/files/{file_name}")
    public void deleteFile(@PathVariable(value = "project_id") UUID projectId,
                           @PathVariable(value = "file_name") String fileName) {
        projectRepository.findById(projectId).ifPresent(project -> {
            String key = project.getId() + "/" + fileName;
            s3.deleteObject(bucket, key);
            projectFileRepository.deleteByName(fileName);
        });
    }

    @GetMapping("/projects/{project_id}/files/{file_name}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "project_id") UUID projectId,
                                           @PathVariable(value = "file_name") String fileName) throws IOException {
        String key = projectId + "/" + fileName;
        return Util.download(s3, bucket, key);
    }

    @PostMapping("/projects/{project_id}/specialties/{spec_id}")
    public Project addSpecialtyToProject(@PathVariable(value = "project_id") UUID projectId,
                                         @PathVariable(value = "spec_id") UUID specId) {
        return projectRepository.findById(projectId).map(project ->
            specialtyRepository.findById(specId).map(specialty -> {
                ProjectSpecialty projectSpecialty = new ProjectSpecialty();
                projectSpecialty.setProject(project);
                projectSpecialty.setSpecialty(specialty);
                projectSpecialtyRepository.save(projectSpecialty);
                return projectRepository.save(project);
        }).orElseThrow(Util.notFound(specId, Specialty.class))).orElseThrow(Util.notFound(projectId, Project.class));
    }

    @Transactional
    @DeleteMapping("/projects/{project_id}/specialties/{spec_id}")
    public void removeSpecialtyFromProject(@PathVariable(value = "project_id") UUID projectId,
                                              @PathVariable(value = "spec_id") UUID specId) {
        projectSpecialtyRepository.deleteProjectSpecialtiesByProjectIdAndSpecialtyId(projectId, specId);
    }

    @PostMapping("/projects/{project_id}/invite/{sub_id}")
    public ProjectInvite inviteSubContractor(@PathVariable(value = "project_id") UUID propId,
                                             @PathVariable(value = "sub_id") UUID subId) {
        return projectRepository.findById(propId).map(project ->
                contractorRepository.findById(subId).map(contractor -> {
                    ProjectInvite projectInvite = new ProjectInvite();
                    projectInvite.setProject(project);
                    projectInvite.setSubContractor(contractor);
                    return projectInviteRepository.save(projectInvite);
                }).orElseThrow(Util.notFound(subId, Contractor.class))).orElseThrow(Util.notFound(propId, Proposal.class));
    }

    @DeleteMapping("/projects/invites/{invite_id}")
    public void deleteInvite(@PathVariable(value = "invite_id") UUID inviteId) {
        projectInviteRepository.deleteById(inviteId);
    }

    @GetMapping("/projects/invites/{sub_id}")
    public Page<Project> findProjectInviteForSubContractor(@PathVariable(value = "sub_id") UUID subId, Pageable pageable) {
        Page<ProjectInvite> page = projectInviteRepository.findBySubContractorId(subId, pageable);
        List<Project> content = page.stream().map(ProjectInvite::getProject).collect(Collectors.toList());
        PageImpl<Project> projects = new PageImpl<>(content, pageable, page.getTotalElements());
        return projects;
    }

    @GetMapping("/projects/{project_id}/invites")
    public List<Contractor> findInviteSubContractorByProject(@PathVariable(value = "project_id") UUID projectId) {
        return projectInviteRepository.findByProjectId(projectId).stream().map(ProjectInvite::getSubContractor).collect(Collectors.toList());
    }
}
