package com.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PressCalcResponse {
    private String pressSize;
    private int volume;
    private int noOfPress;
    private int noOfBatch;
    private int noOfChamber;
    private int totalVolume;
    private int feedPumpFlow;
    private int sqWaterUsed;
    private int sqTankCap;
    private int cw1PWaterUsed;
    private int cw1CWaterUsed;
    private int cwTankCap;
}
