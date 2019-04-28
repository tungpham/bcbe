package com.tung.bcbe.controller;

import com.tung.bcbe.model.Option;
import com.tung.bcbe.repository.CategoryRepository;
import com.tung.bcbe.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

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
}
