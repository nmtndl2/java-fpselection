package com.task.mapper;

import com.task.dto.productRequest.PressRequest;
import com.task.dto.response.PressResponse;
import com.task.entities.product.Press;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PressMapper {

    Press reqToEntity(PressRequest pressRequest);

    PressResponse entityToRes(Press press);
}
