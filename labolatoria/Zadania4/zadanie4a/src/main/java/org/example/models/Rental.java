package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {
    private String id;
    private String vehicleId;
    private String userId;
    private String rentDateTime;
    private String returnDateTime;
}
