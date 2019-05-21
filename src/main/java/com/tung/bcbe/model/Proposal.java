package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "proposal")
@Table(name = "proposal")
public class Proposal extends ID {

    public enum STATUS {
        SUBMITTED,
        AWARDED,
        INACTIVE
    };
    
    @Column
    private String description;
    
    @Column
    private Double budget;

    @Enumerated(EnumType.STRING)
    private STATUS status;
    
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "sub_id", referencedColumnName = "id")
    private Contractor contractor;
}
