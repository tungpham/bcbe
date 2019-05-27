package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "proposalfile")
@Table(name = "proposalfile")
public class ProposalFile extends ID {
    
    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "prop_id", referencedColumnName = "id")
    @JsonIgnore
    private Proposal proposal;
}
