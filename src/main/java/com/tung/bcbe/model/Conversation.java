package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "conversation")
@Table(name = "conversation")
public class Conversation extends ID {

    @ManyToOne
    @JoinColumn(name = "proj_id", referencedColumnName = "id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "con_id", referencedColumnName = "id")
    private Contractor contractor;

//    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Set<ConversationMessage> conversationMessages;
}
