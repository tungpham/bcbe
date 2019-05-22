package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@Entity(name = "contractor_specialty")
@Table(name = "contractor_specialty")
public class ContractorSpecialty extends ID {
    
    @ManyToOne
    @JoinColumn(name = "con_id", referencedColumnName = "id")
    @JsonIgnore
    private Contractor contractor;
    
    @ManyToOne
    @JoinColumn(name = "spec_id", referencedColumnName = "id")
    private Specialty specialty;
}
