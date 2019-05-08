package com.tung.bcbe.controller;

import com.tung.bcbe.model.GenContractor;
import com.tung.bcbe.repository.GenContractorRepository;
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
@RequestMapping("/gencontractors")
public class GenContractorController {

    @Autowired
    GenContractorRepository genContractorRepository;

    @PostMapping
    public GenContractor createGeneralContractor(@Valid @RequestBody GenContractor genContractor) {
        return genContractorRepository.save(genContractor);
    }

    @PutMapping("/{gen_id}")
    public GenContractor updateGeneralContractor(@PathVariable(value = "gen_id") UUID genId, 
                                                 @Valid @RequestBody GenContractor genContractor) {
        GenContractor current = genContractorRepository.findById(genId).orElseThrow(Util.notFound(genId));
        genContractor.getAddress().setCreatedAt(current.getAddress().getCreatedAt());
        genContractor.getAddress().setUpdatedAt(new Date());
        genContractor.setCreatedAt(current.getCreatedAt());
        genContractor.setUpdatedAt(new Date());
        return genContractorRepository.save(genContractor);
    }}
