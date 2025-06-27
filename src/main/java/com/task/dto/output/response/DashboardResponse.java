package com.task.dto.output.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private SlurryResponse slurryResponse;
    private List<PressDataResponse> pressDataResponse;
    private List<PressTResponse> pressTResponse;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private List<String> warnings;

}