package com.task.service.product;

import com.task.dto.product.request.PlateRequest;
import com.task.dto.response.PlateResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface PlateService {
    PlateResponse addPlate(PlateRequest plateRequest);

    PlateResponse updatePlate(Long plateId, @Valid PlateRequest plateRequest);

    List<PlateResponse> getAll();
    PlateResponse getById(Long plateId);

    String deleteById(Long plateId);

    List<String> getPressSizeByPlateType(String plateType);

    List<String> findAllPressSize();
}
