package com.tung.bcbe.controller;

import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.GenContractor;
import com.tung.bcbe.repository.AddressRepository;
import com.tung.bcbe.repository.GenContractorRepository;
import lombok.extern.slf4j.Slf4j;
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
@RestController
@RequestMapping("/gencontractors")
@Slf4j
public class GenContractorController {

    @Autowired 
    GenContractorRepository repository;
    
    @Autowired
    AddressRepository addressRepository;
    
    @PostMapping
    public GenContractor create(@Valid @RequestBody GenContractor genContractor) {
        return repository.save(genContractor);
    }
    
    @PostMapping("/{gen_id}")
    public GenContractor upsertAddress(@PathVariable(name = "gen_id") UUID genId,
                                @Valid @RequestBody GenContractor genContractor) {
        return repository.findById(genId).map(gen -> {
            Address existing = gen.getAddress();
            Address update = genContractor.getAddress();
            if (existing != null) {
                existing.setName(update.getName());
                existing.setStreet(update.getStreet());
                existing.setCity(update.getCity());
                existing.setPhone(update.getPhone());
            } else {
                /*
                existing == null, means either no address ever exist for this user (email), or the address was created
                for subContractor, and we just want to copy the address here to genContractor
                 */
                if (update.getId() != null) {
                    update = addressRepository.findById(update.getId()).orElseThrow(Util.notFound(update.getId()));
                }
                gen.setAddress(update);
            }
            return repository.save(gen);
        }).orElseThrow(Util.notFound(genId));
    }
    
    @GetMapping("/{gen_id}")
    public GenContractor get(@PathVariable(name = "gen_id") UUID genId) {
        return repository.findById(genId).orElseThrow(Util.notFound(genId));
    }
    
    @GetMapping
    public Page<GenContractor> getAll(Pageable pageable) {
        return repository.findAllBy(pageable);
    }
}
