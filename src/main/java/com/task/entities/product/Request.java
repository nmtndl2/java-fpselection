package com.task.entities.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long requestId;

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Sludge name is required")
    private String sludgeName;

    private String sludgeType;

    @NotBlank(message = "Sludge qty is required")
    private long sludgeQty;

    @NotBlank(message = "Dry Solid particle is required")
    private double drySolidParticle;

    private double densityOfDrySolid;

    @NotBlank(message = "Moisture contain is required")
    private int moistureContain;

    @NotBlank(message = "Press size is required")
    private String pressSize;

    @NotBlank(message = "Plate type is Required")
    private String plateType;

    private Time washingT;

    private double cusFeedRate;

}
