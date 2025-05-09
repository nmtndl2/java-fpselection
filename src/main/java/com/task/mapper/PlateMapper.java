package com.task.mapper;

import com.task.dto.productRequest.PlateRequest;
import com.task.dto.response.PlateResponse;
import com.task.entities.product.Plate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlateMapper {


    Plate reqToEntity(PlateRequest plateRequest);
    PlateResponse entityToResp(Plate plate);
}
