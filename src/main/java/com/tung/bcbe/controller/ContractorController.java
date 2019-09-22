package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.dto.ContractorSearchFilter;
import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.ContractorFile;
import com.tung.bcbe.model.ContractorSpecialty;
import com.tung.bcbe.model.Specialty;
import com.tung.bcbe.repository.ContractorFileRepository;
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
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private ContractorFileRepository contractorFileRepository;

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

    /**
     * Special endpoint to delete test account only
     * @param email
     */
    @Transactional
    @DeleteMapping("/{email}")
    public void delete(@PathVariable(name = "email") String email) {
        if (email.equals("test_gen@test.com") || email.equals("test_sub@test.com")) {
            contractorRepository.deleteContractorByEmail(email);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{con_id}")
    public Optional<Contractor> get(@PathVariable(name = "con_id") UUID genId) {
        return contractorRepository.findById(genId).map(contractor -> {
            Set<ContractorFile> files = contractor.getContractorFiles().stream()
                    .filter(file -> !ContractorFile.Type.AVATAR.equals(file.getType()))
                    .collect(Collectors.toSet());
            contractor.setContractorFiles(files);
            return contractor;
        });
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
                existing.setCompany(update.getCompany());
                existing.setWebsite(update.getWebsite());
                existing.setEmployees(update.getEmployees());
                existing.setFounded(update.getFounded());
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
    public void uploadFile(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file,
                           @RequestParam("type") String type) throws IOException {
        upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(), ContractorFile.Type.valueOf(type));
    }

    @PostMapping("/{con_id}/files/upload/multiple")
    public void uploadFile(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile[] files,
                           @RequestParam("type") String type) throws IOException {
        for (MultipartFile file : files) {
            upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(), ContractorFile.Type.valueOf(type));
        }
    }

    @PostMapping("/{con_id}/files/upload/photo")
    public void uploadPhoto(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file) throws IOException {
        upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(), ContractorFile.Type.PICTURE);
    }

    @GetMapping("/{con_id}/photos")
    public List<ContractorFile> getPhotos(@PathVariable(name = "con_id") UUID conId) {
        return contractorFileRepository.findContractorFileByContractorIdAndType(conId, ContractorFile.Type.PICTURE);
    }

    @PostMapping("/{con_id}/files/upload/document")
    public void uploadDocument(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file) throws IOException {
        upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(), ContractorFile.Type.DOCUMENT);
    }

    @PostMapping("/{con_id}/link")
    public void uploadLink(@PathVariable(name = "con_id") UUID conId, @RequestParam("url") String link) throws IOException {
        String encode = URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
        contractorRepository.findById(conId).map(contractor -> {
            ContractorFile contractorFile = ContractorFile.builder()
                    .name(encode)
                    .type(ContractorFile.Type.LINK)
                    .contractor(contractor).build();
            return contractorFileRepository.save(contractorFile);
        }).orElseThrow(Util.notFound(conId, Contractor.class));
    }

    @GetMapping("/{con_id}/link")
    public List<ContractorFile> getLinks(@PathVariable(name = "con_id") UUID conId) {
        return contractorFileRepository.findContractorFileByContractorIdAndType(conId, ContractorFile.Type.LINK);
    }

    @PostMapping("/{con_id}/files/upload/avatar")
    public void uploadAvatar(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file) throws IOException {

        BufferedImage img = ImageIO.read(file.getInputStream());

        int width = 60;
        int height = 60;
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resized, "png", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        //delete old avatar file
        contractorFileRepository.findContractorFileByContractorIdAndType(conId, ContractorFile.Type.AVATAR).forEach(
                contractorFile -> deleteContractorFile(conId, contractorFile.getName())
        );

        upload(conId, file.getOriginalFilename(), os.size(), is, ContractorFile.Type.AVATAR);
    }

    @GetMapping("/{con_id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable(name = "con_id") UUID conId) throws IOException {
        String filename = contractorRepository.findById(conId).map(contractor ->
                contractor.getContractorFiles().stream()
                        .filter(contractorFile -> ContractorFile.Type.AVATAR.equals(contractorFile.getType()))
                        .findAny().map(ContractorFile::getName).orElse(null)
        ).orElseThrow(Util.notFound(conId, Contractor.class));

        if (filename != null)
            return Util.download(s3, bucket, conId + "/" + filename);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public void upload(UUID conId, String fileName, long size, InputStream inputStream, ContractorFile.Type type) {
        contractorRepository.findById(conId).map(contractor -> {
            ContractorFile contractorFile = new ContractorFile();
            contractorFile.setContractor(contractor);
            contractorFile.setName(fileName);
            contractorFile.setType(type);
            String key = contractor.getId() + "/" + fileName;
            try {
                Util.putFile(s3, bucket, key, size, inputStream);
            } catch (Exception e) {
                throw new RuntimeException("Cannot upload " + bucket + "/" + key, e);
            }
            return contractorFileRepository.save(contractorFile);
        }).orElseThrow(Util.notFound(conId, Contractor.class));
    }

    @GetMapping("/{con_id}/files/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable(name = "con_id") UUID conId,
                                           @PathVariable(name = "filename") String filename) throws IOException {
        return Util.download(s3, bucket, conId + "/" + filename);
    }

    @Transactional
    @DeleteMapping("/{con_id}/files/{filename}")
    public void deleteContractorFile(@PathVariable(name = "con_id") UUID conId,
                           @PathVariable(name = "filename") String filename) {
        contractorRepository.findById(conId).map(contractor -> {
            s3.deleteObject(bucket, contractor.getId() + "/" + filename);
            List<ContractorFile> files = contractor.getContractorFiles().stream()
                    .filter(file -> file.getName().equals(filename))
                    .collect(Collectors.toList());
            files.forEach(f -> contractorFileRepository.deleteById(f.getId()));
            return contractor;
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

    @PostMapping("/search")
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
