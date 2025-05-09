package com.task.dto.outputResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PressDataResponse {
    private String pressSize;
    private String plateType;
    private int onePlateVolume;
    private int noOfPress;
    private int noOfBatch;
    private int noOfChamber;
    private String message;
    private int totalVolume;
    private int feedPumpFlow;
    private double airCompressDeli;
    private int sqWaterUsed;
    private int  sqTankCap;
    private double cw1PWaterUsed;
    private double cw1CWaterUsed;
    private int cwTankCap;
}
