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
@Entity(name = "option")
@Table(name = "option")
public class Option extends ID {

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String value;

    @ManyToOne
    @JoinColumn(name = "cat_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;
}
