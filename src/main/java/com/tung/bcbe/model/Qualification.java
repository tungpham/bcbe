package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "qualification")
@Table(name = "qualification")
public class Qualification extends ID {
    
    @Column
    private String name;
    
    @Column
    private String description;
}
