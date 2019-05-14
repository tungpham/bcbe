package com.tung.bcbe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@AllArgsConstructor
@Builder
@Data
@Entity(name = "subcontractor")
@Table(name = "subcontractor")
public class SubContractor extends ID {

    @Id
    @GeneratedValue(generator = "useIdOrGenerate")
    @GenericGenerator(name = "useIdOrGenerate", strategy = "com.tung.bcbe.model.IdGenerator")
    private UUID id;
    
    @Override
    public UUID getId() {
        return this.id;
    }
    
    @Override
    public void setId(UUID id) {
        this.id = id;
    }
    
    @Column(updatable = false)
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
}
