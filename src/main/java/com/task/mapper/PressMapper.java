package com.task.mapper;

import com.task.dto.product.request.PressRequest;
import com.task.dto.response.PressResponse;
import com.task.entities.product.Press;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PressMapper {

    Press reqToEntity(PressRequest pressRequest);

    PressResponse entityToRes(Press press);

    List<PressResponse> entityToRes(List<Press> pressList);
}
