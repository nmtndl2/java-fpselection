package com.task.dto.outputResponse;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private SlurryResponse slurryResponse;
    private List<PressDataResponse> pressDataResponse;
    private List<PressTResponse> pressTResponse;
}