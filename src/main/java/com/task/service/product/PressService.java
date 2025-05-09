package com.task.service.product;

import com.task.dto.productRequest.PressRequest;
import com.task.dto.response.PressResponse;

public interface PressService {
    PressResponse addPress(PressRequest pressRequest);
}
