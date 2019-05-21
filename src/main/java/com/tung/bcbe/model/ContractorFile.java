package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@Entity(name = "contractorfile")
@Table(name = "contractorfile")
@Slf4j
public class ContractorFile extends ID {

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "con_id", referencedColumnName = "id")
    @JsonIgnore
    private Contractor contractor;
}
