package com.tung.bcbe.controller;

import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.ProjectRepository;
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
@RequestMapping("/contractors")
@Slf4j
public class ContractorController {
    
    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    @PostMapping
    public Contractor create(@Valid @RequestBody Contractor contractor) {
        return contractorRepository.save(contractor);
    }
    
    @GetMapping("/{con_id}")
    public Contractor get(@PathVariable(name = "con_id") UUID genId) {
        return contractorRepository.findById(genId).orElseThrow(Util.notFound(genId));
    }
    
    @GetMapping
    public Page<Contractor> getAll(Pageable pageable) {
        return contractorRepository.findAllBy(pageable);
    }
    
    @PostMapping("/{gen_id}")
    public Contractor upsertAddress(@PathVariable(name = "gen_id") UUID genId, 
                                    @Valid @RequestBody Contractor genContractor) {
        return contractorRepository.findById(genId).map(gen -> {
            Address existing = gen.getAddress();
            Address update = genContractor.getAddress();
            if (existing != null) {
                existing.setName(update.getName());
                existing.setStreet(update.getStreet());
                existing.setCity(update.getCity());
                existing.setPhone(update.getPhone());
            } else {
                gen.setAddress(update);
            }
            return contractorRepository.save(gen);
        }).orElseThrow(Util.notFound(genId));
    }
}
