package com.task.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqPumpRequest {

    @NotBlank(message = "Press size is required")
    private String pressSize;

    @NotNull(message = "Maximum time is required")
    private Integer sqMaxTMin;

    @NotNull(message = "Squeezing water is required")
    private Integer sqInletWater;

    private List<SqCalcFRRequest> flowRates = new ArrayList<>();
}
