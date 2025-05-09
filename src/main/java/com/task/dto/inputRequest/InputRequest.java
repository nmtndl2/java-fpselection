package com.task.dto.inputRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputRequest {

    @NotBlank(message = "Client name is required")
    private String clientName;

    private String clientRef;

    @NotBlank(message = "Sludge name is required")
    private String sludgeName;

    @NotBlank(message = "Sludge type is required")
    private String sludgeType;

    @NotNull(message = "Sludge Qty is required")
    private double sludgeQty;

    @NotNull(message = "DrySolid particle is required")
    private double drySolidParticle;

    private double densityOfDrySolid;

    @NotNull(message = "Moisture Contain is required")
    private double moistureContain;

    @NotBlank(message = "Plate type is required")
    private String plateType;

    private int noOfBatch;
    private int noOfPress;
    private LocalTime washingT;
    private LocalTime sqOutletT;


    //Below Variable set on customize
//    private String pressSize;
//    private String plateType;
//    private String washingT;
//    private double cusFeedRate;
}
