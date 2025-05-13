package com.task.mapper;

import com.task.dto.productRequest.SqCalcFRRequest;
import com.task.dto.productRequest.SqPumpRequest;
import com.task.dto.response.SqPumpResponse;
import com.task.entities.product.SqCalcFR;
import com.task.entities.product.SqPump;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SqPumpMapper {

    SqPump toEntity(SqPumpRequest sqPumpRequest);

    SqPumpResponse entityToRes(SqPump sqPump);

    List<SqPump> entityToRes(List<SqPump> sqPumpList);

    List<SqCalcFR> toEntity(List<SqCalcFRRequest> flowRates);
}
