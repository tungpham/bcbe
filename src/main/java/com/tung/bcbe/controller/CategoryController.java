package com.tung.bcbe.controller;

import com.tung.bcbe.model.Category;
import com.tung.bcbe.model.Option;
import com.tung.bcbe.repository.CategoryRepository;
import com.tung.bcbe.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
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
        categoryRepository.findById(catId).ifPresent(cat -> {
            option.setCategory(cat);
            optionRepository.save(option);
        });
        return option;
    }
    
    @GetMapping("/{cat_id}")
    public Optional<Category> get(@PathVariable(name = "cat_id") UUID catId) {
        return categoryRepository.findById(catId);
    }
}
