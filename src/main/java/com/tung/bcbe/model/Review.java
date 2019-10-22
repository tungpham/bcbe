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
@Entity(name = "contractor_review")
@Table(name = "contractor_review")
public class Review extends ID {

    @ManyToOne
    @JoinColumn(name = "con_id", referencedColumnName = "id")
    @JsonIgnore
    private Contractor contractor;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", referencedColumnName = "id")
    @JsonIgnore
    private Contractor reviewer;

    @Column
    private String review;

    @Column
    private int rating;

    @Column
    private String specialty;
}
