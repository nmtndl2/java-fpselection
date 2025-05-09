package com.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PressTCalcResponse {
    private String pressingCT;
    private String feedT;
    private String cakeAirT;
    private String sqInletT;
    private String sqOutletT;
    private String onePlatePST;
    private String onCyclePST;
    private String onePlateCwT;
    private String onCycleCwT;
    private String cakeWT;
}
