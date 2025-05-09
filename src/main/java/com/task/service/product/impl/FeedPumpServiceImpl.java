package com.task.service.product.impl;

import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import com.task.entities.product.FeedPump;
import com.task.exception.AlreadyExistsException;
import com.task.mapper.FeedPumpMapper;
import com.task.repository.product.FeedPumpRepository;
import com.task.service.product.FeedPumpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedPumpServiceImpl implements FeedPumpService {

    private final FeedPumpRepository feedPumpRepository;
    private final FeedPumpMapper feedPumpMapper;

    public FeedPumpResponse createFeedPump(FeedPumpRequest feedPumpRequest){

        boolean isExisit = feedPumpRepository.existsByPressSize(feedPumpRequest.getPressSize());
        if (isExisit) {
            throw new AlreadyExistsException("Already exist press size");
        }

        FeedPump feedPump = feedPumpMapper.reqToEntity(feedPumpRequest);
        feedPump.getChamberRanges().forEach(chamberRange -> chamberRange.setFeedPump(feedPump));
        FeedPump savedFeedPump = feedPumpRepository.save(feedPump);
        return feedPumpMapper.entityToResp(savedFeedPump);

    }

}
