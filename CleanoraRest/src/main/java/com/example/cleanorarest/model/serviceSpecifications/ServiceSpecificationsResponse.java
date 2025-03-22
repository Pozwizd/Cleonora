package com.example.cleanorarest.model.serviceSpecifications;

import com.example.cleanorarest.entity.CleaningSpecifications;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link CleaningSpecifications}
 */
@Data
public class ServiceSpecificationsResponse implements Serializable {
    Long id;
    String name;
    Double baseCost;
    Double complexityCoefficient;
    Double ecoFriendlyExtraCost;
    Double frequencyCoefficient;
    Double locationCoefficient;
    Double timeMultiplier;
    String unit;
    String icon;
}