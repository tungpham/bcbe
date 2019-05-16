package com.tung.bcbe.controller;

import com.tung.bcbe.model.Category;
import com.tung.bcbe.model.Option;
import com.tung.bcbe.repository.CategoryRepository;
import com.tung.bcbe.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OptionRepository optionRepository;

    @PostMapping("/{cat_id}/options")
    public Option addOptionToCriteria(@PathVariable(name = "cat_id") UUID catId, @RequestBody @Valid Option option) {
        return categoryRepository.findById(catId).map(cat -> {
            option.setCategory(cat);
            return optionRepository.save(option);
        }).orElseThrow(Util.notFound(catId));
    }
    
    @GetMapping("/{cat_id}")
    public Category get(@PathVariable(name = "cat_id") UUID catId) {
        return categoryRepository.findById(catId).orElseThrow(Util.notFound(catId));
    }

    @DeleteMapping("/{cat_id}")
    public void delete(@PathVariable(name = "cat_id") UUID catId) {
        categoryRepository.deleteById(catId);
    }
    
    @PutMapping("/{cat_id}")
    public Category edit(@PathVariable(name = "cat_id") UUID catId, @RequestBody Category category) {
        return categoryRepository.findById(catId).map(cat -> {
            if (category.getName() != null) cat.setName(category.getName());
            if (category.getDescription() != null) cat.setDescription(category.getDescription());
            if (category.getType() != null) cat.setType(category.getType());
            if (category.getValue() != null) cat.setValue(category.getValue());
            return categoryRepository.save(cat);
        }).orElseThrow(Util.notFound(catId));
    }
}
