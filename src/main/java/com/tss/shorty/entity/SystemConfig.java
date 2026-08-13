package com.tss.shorty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "system_config")
@Setter
@Getter
public class SystemConfig {
    @Id
    @Column(updatable = false, nullable = false)
    private String code;
    private String description;
    private String value;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "typeId")
    private ValueType valueTypes;
}
