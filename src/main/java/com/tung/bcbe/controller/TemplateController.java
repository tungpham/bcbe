package com.tung.bcbe.controller;

import com.tung.bcbe.model.Category;
import com.tung.bcbe.model.Template;
import com.tung.bcbe.repository.CategoryRepository;
import com.tung.bcbe.repository.OptionRepository;
import com.tung.bcbe.repository.TemplateRepository;
import lombok.extern.slf4j.Slf4j;
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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/templates")
@Slf4j
public class TemplateController {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OptionRepository optionRepository;

    @PostMapping("/{tem_id}/categories")
    public Category addCriteriaToTemplate(@PathVariable(name = "tem_id") UUID temId, @RequestBody @Valid Category category) {
        return templateRepository.findById(temId).map(template -> {
            category.setTemplate(template);
            return categoryRepository.save(category);
        }).orElseThrow(Util.notFound(temId));
    }

    @PostMapping
    public Template createTemplate(@RequestBody @Valid Template template) {
        return templateRepository.save(template);
    }
    
    @GetMapping
    public Page<Template> getAll(Pageable pageable) {
        return templateRepository.findAll(pageable);
    }
    
    @GetMapping("/{tem_id}")
    public Template getTemplate(@PathVariable(name = "tem_id") UUID temId) {
        return templateRepository.findById(temId).orElseThrow(Util.notFound(temId));
    }

    @DeleteMapping("/{tem_id}")
    public void delete(@PathVariable(name = "tem_id") UUID temId) {
        templateRepository.deleteById(temId);
    }
    
    @PutMapping("/{tem_id}")
    public Template edit(@PathVariable(name = "tem_id") UUID temId, @RequestBody @Valid Template template) {
        return templateRepository.findById(temId).map(tem -> {
            if (template.getName() != null) {
                tem.setName(template.getName());
            }
            if (template.getDescription() != null) {
                tem.setDescription(template.getDescription());
            }
            return templateRepository.save(tem);
        }).orElseThrow(Util.notFound(temId));
    }
}
