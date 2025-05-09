package com.task.entities.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Press {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pressId;

    private String pressSize;
    private int maxChamber;

    private LocalTime cakeAirT;
    private LocalTime cyFwdT;
    private LocalTime cyRevT;

    private Boolean dtAvailable;
    private LocalTime dtOpenT;
    private LocalTime dtClosedT;

    private Boolean psAvailable;
    private LocalTime psFwdFPlateT;
    private LocalTime psFwdT;
    private LocalTime psFwdDT;
    private LocalTime psRevT;
    private LocalTime psRevDT;

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
