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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class ContractorFile extends ID {

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "con_id", referencedColumnName = "id")
    @JsonIgnore
    private Contractor contractor;

    public enum Type {
        AVATAR,
        DOCUMENT,
        PICTURE,
        LINK
    }

    @Enumerated(EnumType.STRING)
    private Type type;
}
