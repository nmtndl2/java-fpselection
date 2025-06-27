package com.task.dto.product.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqCalcFRRequest {

    @NotNull(message = "FLow rate is required")
    private double flowRate;
}
