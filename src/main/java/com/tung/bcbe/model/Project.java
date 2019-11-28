package com.tung.bcbe.model;

import com.tung.bcbe.controller.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "project")
@Table(name = "project")
public class Project extends ID {

    public enum Status {
        ACTIVE,
        ONGOING,
        ARCHIVED
    }

    /*
    OWNER_PROJECT is regular project created by owner
    SUBCON_PROJECT is the past project created by contractor in their Profile
     */
    public enum Type {
        OWNER_PROJECT,
        SUBCON_PROJECT
    }

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Double budget;

    @Enumerated(EnumType.STRING)
    private Status status;

    /*
    due date to submit bid
     */
    @Column
    private Date due;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private Integer duration;

    //TODO calculate these dates
    private transient String startDate = LocalDateTime.of(2019, 3, 11, 13, 10, 10).format(Util.formatter);
    private transient String endDate = LocalDateTime.of(2019, 12, 31, 11, 13, 10).format(Util.formatter);
    private transient String city = "Ha Noi";

    private transient String submittedDate;

    /**
     * This is used for sub contractor to add past project, so we know what year the project was completed.
     */
    @Column
    private String year;

    @ManyToOne
    @JoinColumn(name = "gen_id", referencedColumnName = "id", nullable = false)
    private Contractor genContractor;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ProjectFile> projectFiles;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    private Set<ProjectTemplate> projectTemplates;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ProjectSpecialty> projectSpecialties;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectInvite> projectInvites;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ProjectRelationship> relationships;
}
