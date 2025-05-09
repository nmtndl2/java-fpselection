package com.task.service.product.impl;

import com.task.dto.productRequest.PressRequest;
import com.task.dto.response.PressResponse;
import com.task.entities.product.Press;
import com.task.exception.ResourceNotExistsException;
import com.task.mapper.PressMapper;
import com.task.repository.product.PlateRepository;
import com.task.repository.product.PressRepository;
import com.task.service.product.PressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PressServiceImpl implements PressService {

    private final PressRepository pressRepository;
    private final PlateRepository plateRepository;
    private final PressMapper pressMapper;

    @Override
    public PressResponse addPress(PressRequest pressRequest) {
        boolean isExistPress = pressRepository.existsByPressSize(pressRequest.getPressSize());

        if (isExistPress) {
            throw new ResourceNotExistsException("Press size is already exists");
        }

        Press press = pressRepository.save(pressMapper.reqToEntity(pressRequest));
        return pressMapper.entityToRes(press);
    }
}
