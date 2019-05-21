package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "project_template")
@Table(name = "project_template")
@Slf4j
public class ProjectTemplate extends ID {
    
    @ManyToOne
    @JoinColumn(name = "proj_id", referencedColumnName = "id")
    @JsonIgnore
    private Project project;
    
    @ManyToOne
    @JoinColumn(name = "tem_id", referencedColumnName = "id")
    private Template template;
}
