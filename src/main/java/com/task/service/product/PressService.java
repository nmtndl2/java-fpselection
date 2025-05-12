package com.task.service.product;

import com.task.dto.productRequest.PressRequest;
import com.task.dto.response.PressResponse;

import java.util.List;

public interface PressService {
    PressResponse addPress(PressRequest pressRequest);
    List<PressResponse> getAll();
}
