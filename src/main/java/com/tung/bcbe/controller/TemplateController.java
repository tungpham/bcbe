package com.tung.bcbe.controller;

import com.tung.bcbe.model.Criteria;
import com.tung.bcbe.repository.CriteriaRepository;
import com.tung.bcbe.repository.TemplateRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public class TemplateController {

    private CriteriaRepository criteriaRepository;

    private TemplateRepository templateRepository;

    @PostMapping("/templates/{temp_id}/criterias")
    public void addCriteriaToTemplate(@PathVariable(name = "temp_id") String tempId,
                                      @Valid @RequestBody Criteria criteria) {
        
    }
}
