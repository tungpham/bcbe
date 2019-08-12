package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "proposal_option")
@Table(name = "proposal_option")
public class ProposalOption extends ID {

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String value;

    @Column
    private Double budget;

    @Column
    private Double duration;

    @Column
    private String type;
    
    @ManyToOne
    @JoinColumn(name = "prop_id", referencedColumnName = "id")
    @JsonIgnore
    private Proposal proposal;
    
    @ManyToOne
    @JoinColumn(name = "cat_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;
}
