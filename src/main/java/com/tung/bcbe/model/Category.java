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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "category")
@Table(name = "category")
public class Category extends ID {

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String value;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "tem_id", referencedColumnName = "id")
    @JsonIgnore
    private Template template;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Option> optionList;
    
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<ProposalOption> proposalOptions;
}
