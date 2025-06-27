package com.task.service.product;

import com.task.dto.product.request.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;

import java.util.List;


public interface FeedPumpService {
    FeedPumpResponse createFeedPump(FeedPumpRequest feedPumpRequest);
    void deleteFeedPumpById(Long id);

    FeedPumpResponse updateFeedPump(Long id, FeedPumpRequest feedPumpRequest);

    FeedPumpResponse getPump(Long id);

    List<FeedPumpResponse> getAllPump();
}
