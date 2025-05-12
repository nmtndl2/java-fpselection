package com.task.service.product;

import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedPumpService {
    FeedPumpResponse createFeedPump(FeedPumpRequest feedPumpRequest);
//    List<FeedPump> getAllFeedPump();
//    FeedPump getFeedPumpById(Long id);
//    FeedPump updateFeedPump(Long id,FeedPumpRequest updatedFeedPumpRequeset);
//    void deleteFeedPump(Long id);

    void deleteFeedPumpById(Long id);
}
