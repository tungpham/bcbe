package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "template")
@Table(name = "template")
public class Template extends ID {

    @Column
    private String name;

    @Column
    private String description;

    @OneToMany
    @JoinTable(
            name = "TEM_CAT",
            joinColumns = @JoinColumn(name = "TEM_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "CAT_ID", referencedColumnName = "ID")
    )
    private List<Category> categoryList;
}
