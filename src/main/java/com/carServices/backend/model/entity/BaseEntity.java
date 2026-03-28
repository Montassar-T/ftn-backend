package com.carServices.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Date;

public class BaseEntity {
    @Column(name = "created_at")
    @JsonProperty("created_at")
    private Date createdAt;

    @Column(name = "modified_at")
    @JsonProperty("modified_at")
    private Date modifiedAt;

    @Column(name = "deleted_at")
    @JsonProperty("deleted_at")
    private Date deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = new Date();
    }

    @PreRemove
    public void onDelete() {
        deletedAt = new Date();
    }
}