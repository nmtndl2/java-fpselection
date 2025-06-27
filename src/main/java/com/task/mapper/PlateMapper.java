package com.task.mapper;

import com.task.dto.product.request.PlateRequest;
import com.task.dto.response.PlateResponse;
import com.task.entities.product.Plate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlateMapper {


    Plate reqToEntity(PlateRequest plateRequest);
    PlateResponse entityToResp(Plate plate);

    List<PlateResponse> entityToResp(List<Plate> plateList);
}
