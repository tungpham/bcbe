package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
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
    @JoinColumn(name = "tem_id")
    @JsonIgnore
    private Template template;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Option> optionList;
}
