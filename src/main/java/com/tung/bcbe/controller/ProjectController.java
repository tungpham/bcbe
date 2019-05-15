package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.ProjectFile;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.ProjectFileRepository;
import com.tung.bcbe.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/contractors")
@Slf4j
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContractorRepository contractorRepository;
    
    @Autowired
    private ProjectFileRepository projectFileRepository;
    
    @Autowired
    private AmazonS3 s3;
    
    @Value("${S3_BUCKET}")
    private String bucket;

    @PostMapping("/{gen_id}/projects")
    public Project createProject(@PathVariable(value = "gen_id") UUID genId,
                                 @Valid @RequestBody Project project) {
        return contractorRepository.findById(genId).map(genContractor -> {
            project.setContractor(genContractor);
            return projectRepository.save(project);
        }).orElseThrow(Util.notFound(genId));
    }

    @GetMapping("/{gen_id}/projects")
    public Page<Project> getProjectsByGenContractor(@PathVariable(value = "gen_id") UUID genId, Pageable pageable) {
        return projectRepository.findByContractorId(genId, pageable);
    }
    
    @GetMapping("/projects/{project_id}")
    public Optional<Project> getProjectById(@PathVariable(value = "project_id") UUID projectId) {
        return projectRepository.findById(projectId);
    }
    
    @PostMapping("/projects/{project_id}/files/upload")
    public void uploadFiles(@PathVariable(value = "project_id") UUID projectId,
                                      @RequestParam("file") MultipartFile file,
                                      RedirectAttributes redirectAttributes) {
        projectRepository.findById(projectId).ifPresent(project -> {
            try {
                File f = new File(file.getOriginalFilename());
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(file.getBytes());
                fos.close();

                ProjectFile projectFile = new ProjectFile();
                projectFile.setName(file.getOriginalFilename());
                projectFile.setProject(project);
                
                String key = project.getId() + "/" + file.getOriginalFilename();
                
                s3.putObject(bucket, key, f);

                projectFileRepository.save(projectFile);
                
            } catch (IOException e) {
                log.error("error uploading file", e);
                throw new RuntimeException(e);
            }
        });
    }
    
    @DeleteMapping("/projects/{project_id}/files/{file_name}")
    public void deleteFile(@PathVariable(value = "project_id") UUID projectId, 
                           @PathVariable(value = "file_name") String fileName) {
        projectRepository.findById(projectId).ifPresent(project -> {
            String key = project.getId() + "/" + fileName;
            s3.deleteObject(bucket, key);
            projectFileRepository.deleteByName(fileName);
        });
    }
}
