package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "project")
@Table(name = "project")
@Slf4j
public class Project extends ID {

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Double budget;
    
    @ManyToOne
    @JoinColumn(name = "gen_id", referencedColumnName = "id", nullable = false)
    private Contractor contractor;
    
    @OneToMany(mappedBy = "project")
    private List<ProjectFile> projectFiles;
}
