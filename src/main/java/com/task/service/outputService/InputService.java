package com.task.service.outputService;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.outputResponse.DashboardResponse;

public interface InputService {
    DashboardResponse calculateDashboardData(InputRequest inputRequest);
}
