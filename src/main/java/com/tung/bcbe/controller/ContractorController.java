package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.dto.ContractorSearchFilter;
import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.ContractorFile;
import com.tung.bcbe.model.ContractorSpecialty;
import com.tung.bcbe.model.Specialty;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.ContractorSpecialtyRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.SpecialtyRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/contractors")
@Slf4j
public class ContractorController {
    
    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private SpecialtyRepository specialtyRepository;
    
    @Autowired
    private ContractorSpecialtyRepository contractorSpecialtyRepository;
    
    @Autowired
    private AmazonS3 s3;

    @Value("${S3_BUCKET_CON}")
    private String bucket;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @PostMapping
    public Contractor create(@Valid @RequestBody Contractor contractor) {
        contractor.setStatus(Contractor.STATUS.PENDING);
        return contractorRepository.save(contractor);
    }
    
    @GetMapping("/{con_id}")
    public Optional<Contractor> get(@PathVariable(name = "con_id") UUID genId) {
        return contractorRepository.findById(genId);
    }
    
    @GetMapping
    public Page<Contractor> getAll(Pageable pageable) {
        return contractorRepository.findAllBy(pageable);
    }
    
    @PostMapping("/{con_id}")
    public Contractor edit(@PathVariable(name = "con_id") UUID genId, 
                                    @Valid @RequestBody Contractor genContractor) {
        return contractorRepository.findById(genId).map(gen -> {
            Address existing = gen.getAddress();
            Address update = genContractor.getAddress();
            if (existing != null && update != null) {
                existing.setName(update.getName());
                existing.setStreet(update.getStreet());
                existing.setCity(update.getCity());
                existing.setPhone(update.getPhone());
            } else {
                gen.setAddress(update);
            }

            if (genContractor.getStatus() != null) {
                gen.setStatus(genContractor.getStatus());
            }
            if (genContractor.getStatusReason() != null) {
                gen.setStatusReason(genContractor.getStatusReason());
            }
            
            return contractorRepository.save(gen);
        }).orElseThrow(Util.notFound(genId, Contractor.class));
    }

    @PostMapping("/{con_id}/files/upload")
    public void uploadFile(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file) {
        upload(conId, file);
    }
    
    @PostMapping("/{con_id}/files/upload/multiple")
    public void uploadFile(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile[] files) {
        for (MultipartFile file : files) {
            upload(conId, file);
        }
    }

    public void upload(UUID conId, MultipartFile file) {
        contractorRepository.findById(conId).map(contractor -> {
            ContractorFile contractorFile = new ContractorFile();
            contractorFile.setContractor(contractor);
            contractorFile.setName(file.getOriginalFilename());
            contractor.getContractorFiles().add(contractorFile);
            String key = contractor.getId() + "/" + file.getOriginalFilename();
            try {
                Util.putFile(s3, bucket, key, file);
            } catch (IOException e) {
                throw new RuntimeException("Cannot upload " + bucket + "/" + key, e);
            }
            return contractorRepository.save(contractor);
        }).orElseThrow(Util.notFound(conId, Contractor.class));
    }
    
    @GetMapping("/{con_id}/files/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable(name = "con_id") UUID conId,
                                           @PathVariable(name = "filename") String filename) throws IOException {
        return Util.download(s3, bucket, conId + "/" + filename);
    }

    @Transactional
    @DeleteMapping("/{con_id}/files/{filename}")
    public void deleteFile(@PathVariable(name = "con_id") UUID conId,
                           @PathVariable(name = "filename") String filename) {
        contractorRepository.findById(conId).map(contractor -> {
            s3.deleteObject(bucket, contractor.getId() + "/" + filename);
            contractor.getContractorFiles().removeIf(contractorFile -> contractorFile.getName().equals(filename));
            return contractorRepository.save(contractor);
        }).orElseThrow(Util.notFound(conId, Contractor.class));
    }
    
    @PostMapping("/{con_id}/specialties/{spec_id}")
    public Contractor addSpecialtyToContractor(@PathVariable(name = "con_id") UUID conId,
                                               @PathVariable(name = "spec_id") UUID specId) {
        return contractorRepository.findById(conId).map(contractor -> {
            specialtyRepository.findById(specId).map(specialty -> {
                ContractorSpecialty contractorSpecialty = new ContractorSpecialty();
                contractorSpecialty.setContractor(contractor);
                contractorSpecialty.setSpecialty(specialty);
                return contractorSpecialtyRepository.save(contractorSpecialty);
            }).orElseThrow(Util.notFound(specId, Specialty.class));
            return contractorRepository.save(contractor);
        }).orElseThrow(Util.notFound(conId, Contractor.class));
    }
    
    @Transactional
    @DeleteMapping("/{con_id}/specialties/{spec_id}")
    public void removeSpecialtyFromContractor(@PathVariable(name = "con_id") UUID conId,
                                              @PathVariable(name = "spec_id") UUID specId) {
        contractorSpecialtyRepository.deleteContractorSpecialtiesByContractorIdAndSpecialtyId(conId, specId);
    }
    
    @GetMapping("/search")
    public List<Contractor> search(@RequestBody ContractorSearchFilter filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contractor> query = cb.createQuery(Contractor.class);
        Root<Contractor> contractor = query.from(Contractor.class);
        
        Join addressJoin = contractor.join("address");
        
        Predicate predicate = cb.conjunction();
        
        if (StringUtils.isNotBlank(filter.getName())) {
            Path<String> name = addressJoin.get("name");
            predicate = cb.like(name, like(filter.getName()));
        }
        
        if (StringUtils.isNotBlank(filter.getCity())) {
            Path<String> city = addressJoin.get("city");
            predicate = cb.and(predicate, cb.like(city, like(filter.getCity())));
        }
        
        if (filter.getSpecialty() != null) {
            Join contractorSpecialty = contractor.join("contractorSpecialties");
            Join specialty = contractorSpecialty.join("specialty");
            Path<String> specialtyName = specialty.get("name");
            CriteriaBuilder.In<String> inClause = cb.in(specialtyName);
            for (String s : filter.getSpecialty()) {
                inClause.value(s);
            }
            predicate = cb.and(predicate, inClause);
        }
        
        query.select(contractor).where(predicate);
        
        return entityManager.createQuery(query).getResultList();
    }
    
    public String like(String s) {
        return "%"+s+"%";
    }
}
