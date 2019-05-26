package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.ProjectFile;
import com.tung.bcbe.model.ProjectSpecialty;
import com.tung.bcbe.model.ProjectTemplate;
import com.tung.bcbe.model.Specialty;
import com.tung.bcbe.model.Template;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.ProjectFileRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.ProjectSpecialtyRepository;
import com.tung.bcbe.repository.ProjectTemplateRepository;
import com.tung.bcbe.repository.SpecialtyRepository;
import com.tung.bcbe.repository.TemplateRepository;
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
import java.util.List;
import java.util.UUID;

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
    private AmazonS3 s3;
    
    @Value("${S3_BUCKET}")
    private String bucket;

    @PostMapping("/contractors/{gen_id}/projects")
    public Project createProject(@PathVariable(value = "gen_id") UUID genId,
                                 @Valid @RequestBody Project project) {
        return contractorRepository.findById(genId).map(genContractor -> {
            project.setGenContractor(genContractor);
            return projectRepository.save(project);
        }).orElseThrow(Util.notFound(genId, Contractor.class));
    }

    @GetMapping("/contractors/{gen_id}/projects")
    public Page<Project> getProjectsByGenContractor(@PathVariable(value = "gen_id") UUID genId, Pageable pageable) {
        return projectRepository.findByGenContractorId(genId, pageable);
    }
    
    @GetMapping("/projects/{project_id}")
    public Project getProjectById(@PathVariable(value = "project_id") UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow(Util.notFound(projectId, Project.class));
    }

    @GetMapping("/projects")
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
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
            return projectRepository.save(prj);
        }).orElseThrow(Util.notFound(projectId, Project.class));
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
}
