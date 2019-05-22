package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "specialty")
@Table(name = "specialty")
public class Specialty extends ID {
    
    @Column
    private String name;
    
    @Column
    private String description;
    
    @Column
    private String value;
    
    @OneToMany(mappedBy = "specialty")
    private Set<ContractorSpecialty> contractorSpecialties;
    
    @OneToMany(mappedBy = "specialty")
    private Set<ProjectSpecialty> projectSpecialties;
}
