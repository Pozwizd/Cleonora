package com.example.cleanorarest.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Cleaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "cleaning_specifications_id", nullable = false)
    private CleaningSpecifications cleaningSpecifications;
}
