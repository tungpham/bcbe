package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.dto.ContractorSearchFilter;
import com.tung.bcbe.dto.ContractorSearchResultDto;
import com.tung.bcbe.dto.FileNoteDto;
import com.tung.bcbe.dto.GetContractorRatingResponse;
import com.tung.bcbe.dto.ReviewDTO;
import com.tung.bcbe.dto.ReviewSummary;
import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.ContractorFAQ;
import com.tung.bcbe.model.ContractorFile;
import com.tung.bcbe.model.ContractorSpecialty;
import com.tung.bcbe.model.Review;
import com.tung.bcbe.model.Specialty;
import com.tung.bcbe.repository.ContractorFAQRepository;
import com.tung.bcbe.repository.ContractorFileRepository;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.ContractorSpecialtyRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.ReviewRepository;
import com.tung.bcbe.repository.SpecialtyRepository;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    private ContractorFAQRepository contractorFAQRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AmazonS3 s3;

    @Value("${S3_BUCKET_CON}")
    private String bucket;

    @Autowired
    private Auth0Util auth0Util;

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> defaultFAQs = Arrays.asList(
            "What should the customer know about your pricing (e.g., discounts, fees)?",
            "What is your typical process for working with a new customer?",
            "What education and/or training do you have that relates to your work?"
    );


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
    public Optional<Contractor> getContractorById(@PathVariable(name = "con_id") UUID genId) {

//        auth0Util.validateContractorId(genId.toString());

        return contractorRepository.findById(genId).map(contractor -> {
            Set<ContractorFile> files = contractor.getContractorFiles().stream()
                    .filter(file -> !ContractorFile.Type.AVATAR.equals(file.getType()))
                    .collect(Collectors.toSet());
            contractor.setContractorFiles(files);

            //TODO implement this
            contractor.setReviewSummary(getReviewAndRatingSummary());
            return contractor;
        });
    }

    @GetMapping
    public Page<Contractor> getAll(Pageable pageable) {
        return contractorRepository.findAllBy(pageable);
    }

    @PostMapping("/{con_id}")
    public Contractor editContractorById(@PathVariable(name = "con_id") UUID genId, @Valid @RequestBody Contractor genContractor) {
        return contractorRepository.findById(genId).map(gen -> {
            Address existing = gen.getAddress();
            Address update = genContractor.getAddress();
            if (existing != null && update != null) {
                if (update.getName() != null) {
                    existing.setName(update.getName());
                }
                if (update.getStreet() != null) {
                    existing.setStreet(update.getStreet());
                }
                if (update.getCity() != null) {
                    existing.setCity(update.getCity());
                }
                if (update.getPhone() != null) {
                    existing.setPhone(update.getPhone());
                }
                if (update.getCompany() != null) {
                    existing.setCompany(update.getCompany());
                }
                if (update.getWebsite() != null) {
                    existing.setWebsite(update.getWebsite());
                }
                if (update.getEmployees() != null) {
                    existing.setEmployees(update.getEmployees());
                }
                if (update.getFounded() != null) {
                    existing.setFounded(update.getFounded());
                }
                if (update.getIntroduction() != null) {
                    existing.setIntroduction(update.getIntroduction());
                }
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

    @ApiOperation(value = "upload file related to contractor")
    @PostMapping("/{con_id}/files/upload")
    public void uploadFile(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file,
                           @RequestParam("type") String type) throws IOException {
        upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(), ContractorFile.Type.valueOf(type), null);
    }

    @PostMapping("/{con_id}/files/upload/multiple")
    public void uploadMultipleFiles(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile[] files,
                           @RequestParam("type") String type) throws IOException {
        for (MultipartFile file : files) {
            upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(), ContractorFile.Type.valueOf(type), null);
        }
    }

    @ApiOperation(value = "Convenient method to upload photos (in Photos and Video section) related to contractor")
    @PostMapping("/{con_id}/files/upload/photo")
    public void uploadPhoto(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file,
                            @RequestParam(value = "note", defaultValue = "") String note) throws IOException {
        upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(), ContractorFile.Type.PICTURE, note);
    }

    @ApiOperation(value = "Convenient method to get contractor's files of type PICTURE")
    @GetMapping("/{con_id}/photos")
    public List<ContractorFile> getPhotos(@PathVariable(name = "con_id") UUID conId) {
        return contractorFileRepository.findContractorFileByContractorIdAndType(conId, ContractorFile.Type.PICTURE);
    }

    @ApiOperation(value = "add note to a file")
    @PostMapping("/files/{file_id}/note")
    public ContractorFile addFileNote(@PathVariable(name = "file_id") UUID fileId, @RequestBody FileNoteDto data) {
        return contractorFileRepository.findById(fileId).map(file -> {
            try {
                file.setNote(URLEncoder.encode(data.getNote(), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                log.error("Fail to add note to file {}", fileId, e);
            }
            return contractorFileRepository.save(file);
        }).orElseThrow(Util.notFound(fileId, ContractorFile.class));
    }

    @ApiOperation(value = "Convenient method to upload contractor document, such as licenses and permit")
    @PostMapping("/{con_id}/files/upload/document")
    public void uploadDocument(@PathVariable(name = "con_id") UUID conId, @RequestParam("file") MultipartFile file,
                               @RequestParam(value = "note", defaultValue = "") String note) throws IOException {
        upload(conId, file.getOriginalFilename(), file.getSize(), file.getInputStream(),
                ContractorFile.Type.DOCUMENT, StringUtils.isBlank(note) ? null : note);
    }

    @ApiOperation(value = "Convenient method to retrieve contractor's files of type DOCUMENT")
    @GetMapping("/{con_id}/files/document")
    public List<ContractorFile> getDocuments(@PathVariable(name = "con_id") UUID conId) {
        return contractorFileRepository.findContractorFileByContractorIdAndType(conId, ContractorFile.Type.DOCUMENT);
    }

    @PostMapping("/{con_id}/link")
    public void addLink(@PathVariable(name = "con_id") UUID conId, @RequestParam("url") String link,
                        @RequestParam(value = "type", defaultValue = "") String type) throws IOException {
        if (StringUtils.isBlank(link)) {
            return;
        }
        String encode = URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
        contractorRepository.findById(conId).map(contractor -> {
            if (StringUtils.isNotBlank(type)) {
                List<ContractorFile> existing = getLinks(conId, type);
                if (existing.size() != 0) {
                    ContractorFile cf = existing.get(0);
                    cf.setName(encode);
                    return contractorFileRepository.save(cf);
                }
            }

            ContractorFile contractorFile = ContractorFile.builder()
                    .name(encode)
                    .type(StringUtils.isBlank(type) ? ContractorFile.Type.LINK : ContractorFile.Type.valueOf(type))
                    .contractor(contractor).build();
            return contractorFileRepository.save(contractorFile);
        }).orElseThrow(Util.notFound(conId, Contractor.class));
    }

    @ApiOperation(value = "Convenient api to retrieve all contractor links, ie. facebook/twitter/instagram and video url")
    @GetMapping("/{con_id}/link")
    public List<ContractorFile> getLinks(@PathVariable(name = "con_id") UUID conId,
                                         @RequestParam(value = "type", defaultValue = "") String type) {
        if (StringUtils.isBlank(type)) {
            return contractorFileRepository.findContractorFileByContractorIdAndTypeIn(conId, Arrays.asList(
                    ContractorFile.Type.LINK,
                    ContractorFile.Type.FACEBOOK,
                    ContractorFile.Type.INSTAGRAM,
                    ContractorFile.Type.TWITTER));
        } else {
            return contractorFileRepository.findContractorFileByContractorIdAndType(conId, ContractorFile.Type.valueOf(type));
        }
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
                contractorFile -> deleteContractorFileById(conId, contractorFile.getId())
        );

        upload(conId, file.getOriginalFilename(), os.size(), is, ContractorFile.Type.AVATAR, null);
    }

    @GetMapping("/{con_id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable(name = "con_id") UUID conId) throws IOException {
        String filename = contractorRepository.findById(conId).map(contractor ->
                contractor.getContractorFiles().stream()
                        .filter(contractorFile -> ContractorFile.Type.AVATAR.equals(contractorFile.getType()))
                        .findAny().map(ContractorFile::getName).orElse(null)
        ).orElseThrow(Util.notFound(conId, Contractor.class));

        if (filename != null) {
            return Util.download(s3, bucket, conId + "/" + filename);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private void upload(UUID conId, String fileName, long size, InputStream inputStream,
                                  ContractorFile.Type type, String note) {
        contractorRepository.findById(conId).map(contractor -> {
            ContractorFile contractorFile = new ContractorFile();
            contractorFile.setContractor(contractor);
            contractorFile.setName(fileName);
            contractorFile.setType(type);
            contractorFile.setNote(note);
            String key = getConFilePath(contractor.getId(), fileName);
            try {
                Util.putFile(s3, bucket, key, size, inputStream);
            } catch (Exception e) {
                throw new RuntimeException("Cannot upload " + bucket + "/" + key, e);
            }
            return contractorFileRepository.save(contractorFile);
        }).orElseThrow(Util.notFound(conId, Contractor.class));
    }

    @ApiOperation(value = "get contractor's files by file name", notes = "Contractors files are license's images, " +
            "photos, videos, etc.")
    @GetMapping("/{con_id}/files/{filename}")
    public ResponseEntity<byte[]> getContractorFileByFileName(@PathVariable(name = "con_id") UUID conId,
                                           @PathVariable(name = "filename") String filename) throws IOException {
        return Util.download(s3, bucket, getConFilePath(conId, filename));
    }

    @ApiOperation(value = "get contractor's files by file id", notes = "Contractors files are license's images, " +
            "photos, videos, etc.")
    @GetMapping("/{con_id}/files")
    public ResponseEntity<byte[]> getContractorFileByFileId(@PathVariable(name = "con_id") UUID conId,
                                                            @RequestParam UUID id) {
        return contractorFileRepository.findById(id).map(file -> {
            try {
                return Util.download(s3, bucket, getConFilePath(conId, file.getName()));
            } catch (IOException e) {
                log.error("Failed to get file {}", id, e);
            }
            return null;
        }).orElseThrow(Util.notFound(id, ContractorFile.class));
    }

//    @Transactional
//    @DeleteMapping("/{con_id}/files/{filename}")
//    public void deleteContractorFileByName(@PathVariable(name = "con_id") UUID conId,
//                           @PathVariable(name = "filename") String filename) {
//        contractorRepository.findById(conId).map(contractor -> {
//            s3.deleteObject(bucket, getConFilePath(contractor.getId(), filename));
//            List<ContractorFile> files = contractor.getContractorFiles().stream()
//                    .filter(file -> file.getName().equals(filename))
//                    .collect(Collectors.toList());
//            files.forEach(f -> contractorFileRepository.deleteById(f.getId()));
//            return contractor;
//        }).orElseThrow(Util.notFound(conId, Contractor.class));
//    }

    @Transactional
    @DeleteMapping("/{con_id}/files/{id}")
    public void deleteContractorFileById(@PathVariable(name = "con_id") UUID conId,
                                           @PathVariable(name = "id") UUID id) {
        contractorFileRepository.findById(id).map(file -> {
            s3.deleteObject(bucket, getConFilePath(file.getContractor().getId(), file.getName()));
            contractorFileRepository.delete(file);
            return file;
        }).orElseThrow(Util.notFound(id, ContractorFile.class));
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

    @ApiOperation(value = "Search contractors by location and by specialty",
            notes = "Specialty is the literal value like Cleaning, Flooring")
    @PostMapping("/search")
    public List<ContractorSearchResultDto> search(@RequestBody ContractorSearchFilter filter, Pageable pageable) {
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

        List<Contractor> result = entityManager.createQuery(query).getResultList();

        return result.stream().map(c -> {
            ContractorSearchResultDto dto = ContractorSearchResultDto.builder()
                    .reviewSummary(getReviewAndRatingSummary())
                    .contractor(c)
                    .build();
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * TODO to be implemented
     * @return
     */
    public ReviewSummary getReviewAndRatingSummary() {
        return ReviewSummary.builder()
                .aReview("Quick turnaround, great product and price, install was the best I’ve ever experienced. " +
                        "I would recommend this company to anyone needing new flooring.")
                .rating(4.8)
                .totalReviews(10)
                .build();
    }

    /**
     * TODO to be implemented
     * @param conId
     * @param emails
     */
    @PostMapping("/{con_id}/request_reviews")
    public void sendReviewRequest(@PathVariable(name = "con_id") UUID conId, @RequestBody String[] emails) {
        Arrays.stream(emails).forEach(log::info);
    }

    /**
     * TODO to be implemented
     * Get summary rating
     * @param conId
     * @return
     */
    @ApiOperation(value = "Get contractor summary rating - # of 1 star ratings, # of 2 stars rating, etc.")
    @GetMapping("/{con_id}/get_reviews")
    public GetContractorRatingResponse getContractorRating(@PathVariable(name = "con_id") UUID conId) {
        GetContractorRatingResponse response = GetContractorRatingResponse.builder()
                .oneStarRating(0)
                .twoStarRating(0)
                .threeStarRating(1)
                .fourStarRating(4)
                .fiveStarRating(5)
                .reviews(10)
                .build();

        return response;
    }

    /**
     * TODO to be implemented
     * @param conId
     * @param pageable
     * @return
     */
    @ApiOperation(value = "Get list of reviews for this contractor")
    @GetMapping("/{con_id}/reviews")
    public List<Review> getContractorReviews(@PathVariable(name = "con_id") UUID conId, Pageable pageable) {
        final List<Review> list = new ArrayList<>();
        contractorRepository.findById(conId).map(contractor -> {
            list.add(Review.builder().contractor(contractor)
                    .reviewer(Contractor.builder()
                            .address(Address.builder().name("customer 1").build())
                            .build())
                    .client("customer 1")
                    .review("extremely helpful and very responsive. I called in the morning requesting an estimate and he arrived later that day. I would highly recommend")
                    .rating(4)
                    .qualities("Flooring")
                    .build());
            list.add(Review.builder().contractor(contractor)
                    .reviewer(Contractor.builder()
                            .address(Address.builder().name("customer 2").build())
                            .build())
                    .client("customer 2")
                    .review("not only was the first to respond (several did not respond), he had a guy out here within an hour to repair")
                    .rating(4)
                    .qualities("Flooring")
                    .build());
            list.add(Review.builder().contractor(contractor)
                    .reviewer(Contractor.builder()
                            .address(Address.builder().name("customer 3").build())
                            .build())
                    .client("customer 3")
                    .review("They got the whole job done in one day and made sure to do everything I asked. Great job guys!")
                    .rating(5)
                    .build());
            list.add(Review.builder().contractor(contractor)
                    .reviewer(Contractor.builder()
                            .address(Address.builder().name("customer 4").build())
                            .build())
                    .client("customer 4")
                    .review(" got a little behind, but they ended up doing a quality job in the end at a very reasonable price.")
                    .rating(4)
                    .build());
            list.add(Review.builder().contractor(contractor)
                    .reviewer(Contractor.builder()
                            .address(Address.builder().name("customer 5").build())
                            .build())
                    .client("customer 5")
                    .review("I would be happy to revise this review if we are able to find a solution to our issue, but this was a botched job from the beginning")
                    .rating(1)
                    .qualities("Flooring")
                    .build());
            return list;
        }).orElseThrow(Util.notFound(conId, Contractor.class));
        return list;
//        return reviewRepository.findAllById(conId, pageable);
    }

    @PostMapping("/reviews/{con_id}/write")
    public void createReview(@PathVariable(name = "con_id") UUID conId, @ModelAttribute ReviewDTO dto,
                             @RequestParam(value = "file", required = false) MultipartFile[] files) {
        Contractor contractor = contractorRepository.findById(conId).orElseThrow(Util.notFound(conId, Contractor.class));
        int size = files == null ? 0 : files.length;
        Review review = Review.builder()
                .review(dto.getReview())
                .rating(dto.getRating())
                .qualities(String.join(", ", dto.getQualities()))
                .contractor(contractor)
                .build();
        reviewRepository.save(review);
    }

    /**
     * TODO to be implemented
     * @param conId
     * @return
     */
    @GetMapping("/{con_id}/faq")
    public List<ContractorFAQ> getFAQ(@PathVariable(name = "con_id") UUID conId) {
        List<ContractorFAQ> faqs = contractorFAQRepository.findAllByContractorId(conId);
        if (faqs.size() == 0) {
            return defaultFAQs.stream()
                    .map(question -> ContractorFAQ.builder().question(question).build())
                    .collect(Collectors.toList());
        } else {
            return faqs;
        }
    }

    @PostMapping("/{con_id}/faq")
    public void saveFAQ(@PathVariable(name = "con_id") UUID conId, @RequestBody List<ContractorFAQ> faqs) {

        Contractor contractor = contractorRepository.findById(conId).orElseThrow(Util.notFound(conId, Contractor.class));

        Map<String, ContractorFAQ> existings = contractorFAQRepository.findAllByContractorId(conId).stream()
                .collect(Collectors.toMap(ContractorFAQ::getQuestion, faq -> faq));

        List<ContractorFAQ> valids = faqs.stream()
                .filter(faq -> defaultFAQs.contains(faq.getQuestion()))
                .map(faq -> {
                    String question = faq.getQuestion();
                    if (existings.containsKey(question)) {
                        existings.get(question).setAnswer(faq.getAnswer());
                        return existings.get(question);
                    }

                    faq.setContractor(contractor);
                    return faq;
                })
                .collect(Collectors.toList());

        contractorFAQRepository.saveAll(valids);
    }

    public String like(String s) {
        return "%"+s+"%";
    }

    public String getConFilePath(UUID id, String filename) {
        return id + "/" + filename;
    }
}
