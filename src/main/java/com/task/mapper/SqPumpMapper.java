package com.task.mapper;

import com.task.dto.productRequest.SqPumpRequest;
import com.task.dto.response.SqPumpResponse;
import com.task.entities.product.SqPump;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SqPumpMapper {

    SqPump toEntity(SqPumpRequest sqPumpRequest);
    SqPumpResponse entityToRes(SqPump sqPump);
}
