package com.task.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChamberRangeRequest {

    @NotBlank(message = "Chamber range is required")
    private String rangeLabel;

    @NotNull(message = "Flow rate is required")
    private int flowRate;
}
