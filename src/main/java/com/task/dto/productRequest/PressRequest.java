package com.task.dto.productRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PressRequest {

    private long pressId;

    @NotBlank(message = "Press size is required")
    private String pressSize;

    @Positive(message = "Maximum chamber must be a positive number")
    private int maxChamber;

    @NotNull(message = "Cake dry air time is required")
    private LocalTime cakeAirT;

    @NotNull(message = "Cylinder forward time is required")
    private LocalTime cyFwdT;

    @NotNull(message = "Cylinder reverse time is required")
    private LocalTime cyRevT;

    @NotNull(message = "Drip Tray availability is required")
    private Boolean dtAvailable;
    private LocalTime dtOpenT;
    private LocalTime dtClosedT;

    @NotNull(message = "Plate Shifter availability is required")
    private Boolean psAvailable;
    private LocalTime psFwdFPlateT;
    private LocalTime psFwdT;
    private LocalTime psFwdDT;
    private LocalTime psRevT;
    private LocalTime psRevDT;

    @NotNull(message = "Cloth Washing availability is required")
    private Boolean cwAvailable;
    private LocalTime cwFwdT;
    private LocalTime cwFwdDT;
    private LocalTime cwRevT;
    private LocalTime cwRevDT;
    private LocalTime cwDownT;
    private LocalTime cwDownDT;
    private LocalTime cwUpT;
    private LocalTime cwUpDT;
    private int cwFlowRate;
}
