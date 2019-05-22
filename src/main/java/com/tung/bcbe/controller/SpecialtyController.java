package com.tung.bcbe.controller;

import com.tung.bcbe.model.Specialty;
import com.tung.bcbe.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/specialties")
public class SpecialtyController {
    
    @Autowired
    private SpecialtyRepository specialtyRepository;
    
    @PostMapping
    public Specialty add(@RequestBody @Valid Specialty specialty) {
        return specialtyRepository.save(specialty);
    }
    
    @GetMapping("/{spec_id}")
    public Specialty get(@PathVariable(name = "spec_id") UUID specId) {
        return specialtyRepository.findById(specId).orElseThrow(Util.notFound(specId, Specialty.class));
    }
    
    @GetMapping
    public Page<Specialty> getAll(Pageable pageable) {
        return specialtyRepository.findAll(pageable);
    }
    
    @PutMapping("/{spec_id}")
    public Specialty edit(@PathVariable(name = "spec_id") UUID specId, 
                          @RequestBody @Valid Specialty specialty) {
        return specialtyRepository.findById(specId).map(spec -> {
            if (specialty.getName() != null) {
                spec.setName(specialty.getName());
            }
            if (specialty.getDescription() != null) {
                spec.setDescription(specialty.getDescription());
            }
            if (specialty.getValue() != null) {
                spec.setValue(specialty.getValue());
            }
            return specialtyRepository.save(spec);
        }).orElseThrow(Util.notFound(specId, Specialty.class));
    }
    
    @DeleteMapping("/{spec_id}")
    public void delete(@PathVariable(name = "spec_id") UUID specId) {
        specialtyRepository.deleteById(specId);
    }
}
