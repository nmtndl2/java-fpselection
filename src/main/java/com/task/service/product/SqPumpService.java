package com.task.service.product;

import com.task.dto.productRequest.SqPumpRequest;
import com.task.dto.response.SqPumpResponse;

public interface SqPumpService {
    SqPumpResponse addSqPump(SqPumpRequest sqPumpDTO);
}
