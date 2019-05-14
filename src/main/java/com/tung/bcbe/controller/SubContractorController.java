package com.tung.bcbe.controller;

import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.SubContractor;
import com.tung.bcbe.repository.AddressRepository;
import com.tung.bcbe.repository.SubContractorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RequestMapping("/subcontractors")
@RestController
public class SubContractorController {

    @Autowired
    SubContractorRepository repository;

    @Autowired
    AddressRepository addressRepository;
    
    @PostMapping
    public SubContractor create(@Valid @RequestBody SubContractor genContractor) {
        return repository.save(genContractor);
    }

    @PostMapping("/{sub_id}")
    public SubContractor upsertAddress(@PathVariable(name = "sub_id") UUID subId,
                                       @Valid @RequestBody SubContractor subContractor) {
        return repository.findById(subId).map(sub -> {
            Address existing = sub.getAddress();
            Address update = subContractor.getAddress();
            if (existing != null) {
                existing.setName(update.getName());
                existing.setStreet(update.getStreet());
                existing.setCity(update.getCity());
                existing.setPhone(update.getPhone());
            } else {
                /*
                existing == null, means either no address ever exist for this user (email), or the address was created
                for genContractor, and we just want to copy the address here to subContractor
                 */
                if (update.getId() != null) {
                    update = addressRepository.findById(update.getId()).orElseThrow(Util.notFound(update.getId()));   
                }
                sub.setAddress(update);
            }
            return repository.save(sub);
        }).orElseThrow(Util.notFound(subId));
    }

    @GetMapping("/{sub_id}")
    public SubContractor get(@PathVariable(name = "sub_id") UUID subId) {
        return repository.findById(subId).orElseThrow(Util.notFound(subId));
    }

    @GetMapping
    public Page<SubContractor> getAll(Pageable pageable) {
        return repository.findAllBy(pageable);
    }
}
