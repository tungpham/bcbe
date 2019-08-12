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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "level")
@Table(name = "level")
public class Level extends ID {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proj_id", referencedColumnName = "id")
    @JsonIgnore
    private Project project;
    
    @Column(updatable = false)
    private int number;
    
    @Column
    private String name;
    
    @Column
    private String description;
    
    @OneToMany(mappedBy = "level", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;
}
