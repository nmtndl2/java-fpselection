package com.task.mapper;

import com.task.dto.productRequest.PlateTypeRequest;
import com.task.dto.response.PlateTypeResponse;
import com.task.entities.product.PlateType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlateTypeMapper {

    PlateType reqToEntity(PlateTypeRequest plateTypeRequest);
    PlateTypeResponse entityToResp(PlateType plateType);
}
