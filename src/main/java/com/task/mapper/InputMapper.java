package com.task.mapper;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import com.task.entities.product.FeedPump;
import com.task.entities.product.Input;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InputMapper {
    Input reqToEntity(InputRequest inputRequest);
}
