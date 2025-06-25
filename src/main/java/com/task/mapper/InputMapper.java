package com.task.mapper;

import com.task.dto.inputRequest.InputRequest;
import com.task.entities.product.Input;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InputMapper {
    Input reqToEntity(InputRequest inputRequest);
}
