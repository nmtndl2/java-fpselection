package com.task.mapper;

import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import com.task.entities.product.FeedPump;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedPumpMapper {

    FeedPump reqToEntity(FeedPumpRequest feedPumpRequest);
    FeedPumpResponse entityToResp(FeedPump feedPump);
}
