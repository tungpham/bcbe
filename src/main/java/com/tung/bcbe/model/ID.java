package com.tung.bcbe.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class ID implements Serializable {

    @Id
    @GeneratedValue(generator = "useIdOrGenerate")
    @GenericGenerator(name = "useIdOrGenerate", strategy = "com.tung.bcbe.model.IdGenerator")
    @Column(name = "id")
    private UUID id;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @LastModifiedDate
    private Date updatedAt;

    @Column
    @LastModifiedBy
    private String updatedBy;
}
