package com.tung.bcbe.controller;

import com.tung.bcbe.model.SubContractor;
import com.tung.bcbe.repository.SubContractorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SubContractorController {

    @Autowired
    private SubContractorRepository subContractorRepository;

    @PostMapping("/subcontractors")
    public SubContractor createSubContractor(@Valid @RequestBody SubContractor subContractor) {
        return subContractorRepository.save(subContractor);
    }
}
