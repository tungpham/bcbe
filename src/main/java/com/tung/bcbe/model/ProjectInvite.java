package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "project_invite")
@Table(name = "project_invite")
public class ProjectInvite extends ID {
    
    @ManyToOne
    @JoinColumn(name = "proj_id", referencedColumnName = "id")
    @JsonIgnore
    private Project project;
    
    @OneToOne
    @JoinColumn(name = "sub_id", referencedColumnName = "id")
    @JsonIgnore
    private Contractor subContractor;
}
