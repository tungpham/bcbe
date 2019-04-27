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

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity
public class TemCat extends ID {

    @ManyToOne
    @JoinColumn(name = "tem_id")
    @JsonIgnore
    private Template template;

    @ManyToOne
    @JoinColumn(name = "cat_id")
    @JsonIgnore
    private Category category;

    @Column
    private String note;
}
