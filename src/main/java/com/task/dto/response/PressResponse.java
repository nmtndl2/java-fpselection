package com.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PressResponse {

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
