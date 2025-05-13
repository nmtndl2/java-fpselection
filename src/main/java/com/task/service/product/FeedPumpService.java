package com.task.service.product;

import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface FeedPumpService {
    FeedPumpResponse createFeedPump(FeedPumpRequest feedPumpRequest);
    void deleteFeedPumpById(Long id);

    FeedPumpResponse updateFeedPump(Long id, FeedPumpRequest feedPumpRequest);

    FeedPumpResponse getPump(Long id);

    List<FeedPumpResponse> getAllPump();
}
