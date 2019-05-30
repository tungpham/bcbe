package com.tung.bcbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "proposalmsgfile")
@Table(name = "proposalmsgfile")
public class ProposalMessageFile extends ID {
    
    @Column
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "msg_id", referencedColumnName = "id")
    @JsonIgnore
    private Message message;
}
