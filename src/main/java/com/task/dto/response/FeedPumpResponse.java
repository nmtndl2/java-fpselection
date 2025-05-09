package com.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedPumpResponse {

    private long id;
    private String pressSize;
    private List<ChamberRangeResponse> chamberRanges = new ArrayList<>();
}
