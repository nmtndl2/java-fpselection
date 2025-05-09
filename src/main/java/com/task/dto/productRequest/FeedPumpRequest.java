package com.task.dto.productRequest;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedPumpRequest {

    @NotBlank(message = "Plate Size is required")
    private String pressSize;
    private List<ChamberRangeRequest> chamberRanges = new ArrayList<>();
}
