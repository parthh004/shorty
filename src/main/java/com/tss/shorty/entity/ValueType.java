package com.tss.shorty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "value_types")
public class ValueType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID typeId;
    private String type;
}
