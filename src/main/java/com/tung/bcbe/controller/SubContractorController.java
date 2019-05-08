package com.tung.bcbe.controller;

import com.tung.bcbe.model.SubContractor;
import com.tung.bcbe.repository.SubContractorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/subcontractors")
public class SubContractorController {

    @Autowired
    private SubContractorRepository subContractorRepository;

    @PostMapping
    public SubContractor createSubContractor(@Valid @RequestBody SubContractor subContractor) {
        return subContractorRepository.save(subContractor);
    }

    @PutMapping("/{sub_id}")
    public SubContractor createSubContractor(@PathVariable(value = "sub_id") UUID subId,
                                             @Valid @RequestBody SubContractor subContractor) {
        SubContractor current = subContractorRepository.findById(subId).orElseThrow(Util.notFound(subId));
        subContractor.getAddress().setCreatedAt(current.getAddress().getCreatedAt());
        subContractor.getAddress().setUpdatedAt(new Date());
        subContractor.setCreatedAt(current.getCreatedAt());
        subContractor.setUpdatedAt(new Date());
        return subContractorRepository.save(subContractor);
    }
}
