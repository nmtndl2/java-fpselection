package com.task.dto.output.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer totalVolume;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer feedPumpFlow;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer airCompressDeli;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Double sqFlowRate;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer sqWaterUsed;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer sqTankCap;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer cw1PWaterUsed;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer cw1CWaterUsed;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer cwTankCap;
}
