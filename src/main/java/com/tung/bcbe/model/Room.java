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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Entity(name = "room")
@Table(name = "room")
public class Room extends ID {

    public enum TYPE {
        LIVINGROOM,
        BATHROOM,
        BEDROOM,
        HALLWAY,
        STAIRS,
        KITCHEN,
        ROOFTOP,
        OTHER
    };

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lvl_id", referencedColumnName = "id")
    @JsonIgnore
    private Level level;

    @Column(updatable = false)
    private int number;

    @Enumerated(EnumType.STRING)
    private TYPE type;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private int w;

    @Column
    private int l;

    @Column
    private int h;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
    private List<Selection> selectionList;

//    @Transient
//    @Getter(AccessLevel.NONE)
//    private Map<UUID, Selection> selectionMap;
//
//    @Transient
//    @JsonGetter
//    public Map<UUID, Selection> getSelectionMap() {
//        return selectionMap;
//    }
}
