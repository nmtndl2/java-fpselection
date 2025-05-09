package com.task.service.product.impl;

import com.task.dto.productRequest.PlateTypeRequest;
import com.task.dto.response.PlateTypeResponse;
import com.task.entities.product.PlateType;
import com.task.exception.AlreadyExistsException;
import com.task.mapper.PlateTypeMapper;
import com.task.repository.product.PlateTypeRepository;
import com.task.service.product.PlateTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlateTypeServiceImpl implements PlateTypeService {

    private final PlateTypeRepository plateTypeRepository;
    private final PlateTypeMapper plateTypeMapper;


    @Override
    public PlateTypeResponse addPlateType(PlateTypeRequest plateTypeRequest) {
        if (plateTypeRepository.existsByTypeName(plateTypeRequest.getTypeName())) {
            throw new AlreadyExistsException("Plate Type already exists");
        }

        PlateType plateType = plateTypeRepository.save(plateTypeMapper.reqToEntity(plateTypeRequest));
        return plateTypeMapper.entityToResp(plateType);
    }

}
