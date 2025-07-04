package com.task.mapper;

import com.task.dto.product.request.PlateTypeRequest;
import com.task.dto.response.PlateTypeResponse;
import com.task.entities.product.PlateType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlateTypeMapper {

    PlateType reqToEntity(PlateTypeRequest plateTypeRequest);
    PlateTypeResponse entityToResp(PlateType plateType);

    List<PlateTypeResponse> entityToResp(List<PlateType> plateTypes);
}
