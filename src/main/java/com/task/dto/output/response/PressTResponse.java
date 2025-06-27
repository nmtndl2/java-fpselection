package com.task.dto.output.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalTime;

@Data
public class PressTResponse {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime pressingCT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime feedT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime cakeAirT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime sqInletT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime sqOutletT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime onePlatePsT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime onCyclePsT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime onePlateCwT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime onCycleCwT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private LocalTime cakeWT;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String message;
}
