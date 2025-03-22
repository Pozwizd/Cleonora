package com.example.cleanorarest.model.serviceSpecifications;

import com.example.cleanorarest.entity.CleaningSpecifications;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


/**
 * DTO for {@link CleaningSpecifications}
 */

@Data
public class ServiceSpecificationsRequest {

    Long id;
    String name;
    Double baseCost;
    Double complexityCoefficient;
    Double ecoFriendlyExtraCost;
    Double frequencyCoefficient;
    Double locationCoefficient;
    Double timeMultiplier;
    String unit;
    MultipartFile icon;

}