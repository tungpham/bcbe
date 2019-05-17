package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteStreams;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private AmazonS3 s3;
    
    @Value("${S3_BUCKET}")
    private String bucket;

    @PostMapping("/contractors/{gen_id}/projects")
    public Project createProject(@PathVariable(value = "gen_id") UUID genId,
                                 @Valid @RequestBody Project project) {
        return contractorRepository.findById(genId).map(genContractor -> {
            project.setContractor(genContractor);
            return projectRepository.save(project);
        }).orElseThrow(Util.notFound(genId));
    }

    @GetMapping("/contractors/{gen_id}/projects")
    public Page<Project> getProjectsByGenContractor(@PathVariable(value = "gen_id") UUID genId, Pageable pageable) {
        return projectRepository.findByContractorId(genId, pageable);
    }
    
    @GetMapping("/projects/{project_id}")
    public Project getProjectById(@PathVariable(value = "project_id") UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow(Util.notFound(projectId));
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
        }).orElseThrow(Util.notFound(projectId));
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

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(file.getSize());
                s3.putObject(bucket, key, file.getInputStream(), objectMetadata);

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
        S3Object s3Object = s3.getObject(bucket, key);
        InputStream is = s3Object.getObjectContent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.copy(is, baos);
        is.close();
        
        return ResponseEntity.ok().contentType(contentType(fileName))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + fileName + "\"")
                .body(baos.toByteArray());
    }

    private MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length-1];
        switch(type) {
            case "txt": return MediaType.TEXT_PLAIN;
            case "png": return MediaType.IMAGE_PNG;
            case "jpg": return MediaType.IMAGE_JPEG;
            default: return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
