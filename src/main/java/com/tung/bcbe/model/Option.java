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
@Entity(name = "option")
@Table(name = "option")
public class Option extends ID {

    @Column
    private String name;

    @Column
    private String type;
    
    @Column
    private String description;

    @Column
    private String value;

    @Column
    private Double budget;
    
    @Column
    private Double duration;
    
    @ManyToOne
    @JoinColumn(name = "cat_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;
}
