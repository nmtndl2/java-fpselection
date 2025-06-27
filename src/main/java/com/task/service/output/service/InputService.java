package com.task.service.output.service;

import com.task.dto.input.request.InputRequest;
import com.task.dto.output.response.DashboardResponse;

public interface InputService {
    DashboardResponse calculateDashboardData(InputRequest inputRequest);
}
