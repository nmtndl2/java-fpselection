package com.task.service.product;

import com.task.dto.product.request.SqPumpRequest;
import com.task.dto.response.SqPumpResponse;
import com.task.entities.product.SqPump;

import java.util.List;

public interface SqPumpService {
    SqPumpResponse addSqPump(SqPumpRequest sqPumpDTO);

    List<SqPump> getAll();

    SqPumpResponse updateSqPump(Long id, SqPumpRequest sqPumpRequest);

    SqPumpResponse getSqPump(Long id);

    String deleteSqPump(Long id);
}
