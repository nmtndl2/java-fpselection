package com.task.service.product;

import com.task.dto.product.request.PressRequest;
import com.task.dto.response.PressResponse;

import java.util.List;

public interface PressService {
    PressResponse addPress(PressRequest pressRequest);
    List<PressResponse> getAll();
    PressResponse updatePress(Long pressId, PressRequest pressRequest);
    void deletePressById(Long pressId);

    PressResponse getPress(Long pressId);
}
