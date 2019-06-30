package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "contractor")
@Table(name = "contractor")
public class Contractor extends ID {
    
    public enum STATUS {
        PENDING,
        ACTIVE,
        REJECTED
    }
    
    @Column(updatable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private STATUS status;
    
    @Column(name = "status_reason")
    private String statusReason;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    
    @OneToMany(mappedBy = "contractor", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ContractorFile> contractorFiles;
    
    @OneToMany(mappedBy = "contractor", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ContractorSpecialty> contractorSpecialties;
}

