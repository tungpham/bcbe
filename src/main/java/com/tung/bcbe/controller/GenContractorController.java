package com.tung.bcbe.controller;

import com.tung.bcbe.model.GenContractor;
import com.tung.bcbe.repository.GenContractorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class GenContractorController {

    @Autowired
    GenContractorRepository genContractorRepository;

    @PostMapping("/gencontractors")
    public GenContractor createGeneralContractor(@Valid @RequestBody GenContractor genContractor) {
        return genContractorRepository.save(genContractor);
    }
}
