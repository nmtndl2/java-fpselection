package com.task.dto.product.request;

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
    private Integer volume;

    @NotNull(message = "Filtration area is required")
    private Double filtrationArea;

    @NumberFormat
    private Integer cakeThk;

    @NumberFormat
    private Integer finalCakeThk;
}