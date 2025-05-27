package com.task.service.product;

import com.task.dto.productRequest.PlateTypeRequest;
import com.task.dto.response.PlateTypeResponse;
import com.task.entities.product.PlateType;

import java.util.List;


public interface PlateTypeService {
    PlateTypeResponse addPlateType(PlateTypeRequest plateTypeRequest);

    String deletePlateType(Long plateTypeId);

    List<PlateType> findAll();

    List<String> getMemPlateSize();

}
