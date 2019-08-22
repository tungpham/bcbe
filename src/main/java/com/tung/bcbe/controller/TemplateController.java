package com.tung.bcbe.controller;

import com.tung.bcbe.model.Category;
import com.tung.bcbe.model.Node;
import com.tung.bcbe.model.Template;
import com.tung.bcbe.repository.CategoryRepository;
import com.tung.bcbe.repository.NodeRepository;
import com.tung.bcbe.repository.OptionRepository;
import com.tung.bcbe.repository.TemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
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

    @Autowired
    private NodeRepository nodeRepository;

    @PostMapping("/{tem_id}/categories")
    public Category addCriteriaToTemplate(@PathVariable(name = "tem_id") UUID temId, @RequestBody @Valid Category category) {
        return templateRepository.findById(temId).map(template -> {
            category.setTemplate(template);
            return categoryRepository.save(category);
        }).orElseThrow(Util.notFound(temId, Template.class));
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
        return templateRepository.findById(temId).orElseThrow(Util.notFound(temId, Template.class));
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
        }).orElseThrow(Util.notFound(temId, Template.class));
    }

    @GetMapping("/nodes")
    public List<Node> getAllRoots() {
        return nodeRepository.findNodeByParentIsNull();
    }

    @PostMapping("/nodes")
    public Node createRoot(@RequestBody @Valid Node root) {
        if (nodeRepository.findByName(root.getName()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate name");
        }
        return nodeRepository.save(root);
    }

    @PostMapping("/nodes/{parent_id}")
    public Node createChild(@PathVariable(name = "parent_id") UUID parentId, @RequestBody @Valid Node child) {
        return nodeRepository.findById(parentId).map(parent -> {
            child.setParent(parent);
            return nodeRepository.save(child);
        }).orElseThrow(Util.notFound(parentId, Node.class));
    }

    @GetMapping("/nodes/{node_id}")
    public Node getNode(@PathVariable(name = "node_id") UUID nodeId) {
        return nodeRepository.findById(nodeId).orElseThrow(Util.notFound(nodeId, Node.class));
    }

    @PutMapping("/nodes/{node_id}")
    public Node editNode(@PathVariable(name = "node_id") UUID nodeId, @RequestBody @Valid Node node) {
        return nodeRepository.findById(nodeId).map(current -> {
            if (StringUtils.compare(node.getType(), current.getType()) != 0)  {
                current.setType(node.getType());
            }
            if (StringUtils.compare(node.getName(), current.getName()) != 0) {
                current.setName(node.getName());
            }
            if (StringUtils.compare(node.getDescription(), current.getDescription()) != 0) {
                current.setDescription(node.getDescription());
            }
            if (StringUtils.compare(node.getValue(), current.getValue()) != 0) {
                current.setValue(node.getValue());
            }
            return nodeRepository.save(current);
        }).orElseThrow(Util.notFound(nodeId, Node.class));
    }

    @DeleteMapping("/nodes/{node_id}")
    public void deleteNode(@PathVariable(name = "node_id") UUID nodeId) {
        nodeRepository.deleteById(nodeId);
    }
}
