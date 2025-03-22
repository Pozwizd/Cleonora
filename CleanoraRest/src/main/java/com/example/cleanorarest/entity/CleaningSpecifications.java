package com.example.cleanorarest.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class CleaningSpecifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double baseCost;
    @Column(nullable = false)
    private Double complexityCoefficient;
    private Double ecoFriendlyExtraCost;
    private Double frequencyCoefficient;
    private Double locationCoefficient;
    private Double timeMultiplier;
    private String unit;
    private String icon;
}