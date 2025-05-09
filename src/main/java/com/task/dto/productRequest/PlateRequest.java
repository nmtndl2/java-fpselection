package com.task.dto.productRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlateRequest {

    private long plateId;

    @NotBlank(message = "Press size is required")
    private String pressSize;

    @NotBlank(message = "Plate Type is required")
    private String plateType;

    @NotNull(message = "Volume is required")
    private int volume;

    @NotNull(message = "Filtration area is required")
    private double filtrationArea;

    @NumberFormat
    private int cakeThk;

    @NumberFormat
    private int finalCakeThk;

}