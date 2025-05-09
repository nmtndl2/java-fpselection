package com.task.dto.response;

import com.task.entities.product.SqCalcFR;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqPumpResponse {

    private String pressSize;
    private int sqMaxTMin;
    private int sqInletWater;
    private List<SqCalcFRResponse> flowRates = new ArrayList<>();
}
