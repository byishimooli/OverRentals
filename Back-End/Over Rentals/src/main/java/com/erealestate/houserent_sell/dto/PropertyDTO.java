
package com.erealestate.houserent_sell.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDTO {
    private String title;
    private String address;
    private String description;
    private double price;
    private int bedrooms;
    private int bathrooms;
    private String propertyType;

    // Getters and setters
}
