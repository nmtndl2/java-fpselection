package com.task.service.outputService;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.outputResponse.DashboardResponse;
import com.task.dto.outputResponse.PressDataResponse;

import java.util.List;

public interface InputService {
    DashboardResponse calculateDashboardData(InputRequest inputRequest);
}
