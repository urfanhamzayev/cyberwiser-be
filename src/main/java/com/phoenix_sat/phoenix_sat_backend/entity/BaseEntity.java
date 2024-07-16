package com.phoenix_sat.phoenix_sat_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    @Column(name = "create_date", nullable = false, updatable = false)
    private Date createDate = new Date();

    @Column(name = "update_date")
    private Date updateDate;

    @PrePersist
    public void initializeDate() {
        this.createDate = new Date();
        this.updateDate = new Date();
    }
}
