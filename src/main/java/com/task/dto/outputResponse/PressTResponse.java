package com.task.dto.outputResponse;

import lombok.Data;

import java.time.LocalTime;

@Data
public class PressTResponse {
    private LocalTime pressingCT;
    private LocalTime feedT;
    private LocalTime cakeAirT;
    private LocalTime sqInletT;
    private LocalTime sqOutletT;
    private LocalTime onePlatePST;
    private LocalTime onCyclePST;
    private LocalTime onePlateCwT;
    private LocalTime onCycleCwT;
    private LocalTime cakeWT;
}
