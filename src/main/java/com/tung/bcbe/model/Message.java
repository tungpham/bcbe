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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "message")
@Table(name = "message")
public class Message extends ID {

    public enum Status {
        READ,
        UNREAD
    }
    
    @Column
    @NotNull
    private String content;
    
    @Column
    private Status status;
    
    @ManyToOne
    @JoinColumn(name = "prop_id", referencedColumnName = "id")
    @JsonIgnore
    private Proposal proposal;
    
    @ManyToOne
    @JoinColumn(name = "from_id", referencedColumnName = "id") 
    private Contractor from;
    
    @ManyToOne
    @JoinColumn(name = "to_id", referencedColumnName = "id")
    private Contractor to;
    
    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER)
    private Set<ProposalMessageFile> proposalMessageFiles;
}
