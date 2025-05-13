package com.task.service.product;

import com.task.dto.productRequest.PlateTypeRequest;
import com.task.dto.response.PlateTypeResponse;

public interface PlateTypeService {
    PlateTypeResponse addPlateType(PlateTypeRequest plateTypeRequest);

    String deletePlateType(Long plateTypeId);
}
