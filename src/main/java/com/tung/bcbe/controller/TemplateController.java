package com.tung.bcbe.controller;

import com.tung.bcbe.model.Category;
import com.tung.bcbe.model.TemCat;
import com.tung.bcbe.model.Template;
import com.tung.bcbe.repository.CategoryRepository;
import com.tung.bcbe.repository.TemCatRepository;
import com.tung.bcbe.repository.TemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class TemplateController {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TemCatRepository temCatRepository;

    @PostMapping("/templates/{tem_id}/{cat_id}")
    public TemCat addCriteriaToTemplate(@PathVariable(name = "tem_id") UUID temId,
                                        @PathVariable(name = "cat_id") UUID catId) throws ExecutionException, InterruptedException {

        CompletableFuture<Template> template = CompletableFuture.supplyAsync(() -> templateRepository.findById(temId))
                .thenApply(x -> x.orElseThrow(() -> new ResourceNotFoundException(temId + " not found")));

        CompletableFuture<Category> category = CompletableFuture.supplyAsync(() -> categoryRepository.findById(catId))
                .thenApply(x -> x.orElseThrow(() -> new ResourceNotFoundException(catId + " not found")));

        return category.thenCombine(template, (cat, tem) -> {
            TemCat temCat = new TemCat();
            temCat.setCategory(cat);
            temCat.setTemplate(tem);
            return temCatRepository.save(temCat);
        }).get();
    }

    @GetMapping("/templates/{tem_id}")
    public Template getTemplate(@PathVariable(name = "tem_id") UUID temId) throws ExecutionException, InterruptedException {
        return templateRepository.findById(temId).orElseThrow(() -> new ResourceNotFoundException());
    }
}
