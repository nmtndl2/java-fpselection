package com.task.service.product;

import com.task.dto.productRequest.PlateRequest;
import com.task.dto.response.PlateResponse;

public interface PlateService {
    PlateResponse addPlate(PlateRequest plateRequest);
}
