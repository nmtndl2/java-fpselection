package com.task.service.product.impl;

import com.task.dto.productRequest.PlateRequest;
import com.task.dto.response.PlateResponse;
import com.task.entities.product.Plate;
import com.task.exception.AlreadyExistsException;
import com.task.exception.ResourceNotExistsException;
import com.task.mapper.PlateMapper;
import com.task.repository.product.PlateRepository;
import com.task.repository.product.PlateTypeRepository;
import com.task.service.product.PlateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlateServiceImpl implements PlateService {

    private final PlateRepository plateRepository;
    private final PlateTypeRepository plateTypeRepository;
    private final PlateMapper plateMapper;

    @Override
    public PlateResponse addPlate(PlateRequest plateRequest) {

        if(!plateTypeRepository.existsByTypeName(plateRequest.getPlateType())) {
            throw new ResourceNotExistsException("Plate type not found");
        }

        if(plateRepository.existsByPressSizeAndPlateType(plateRequest.getPressSize(), plateRequest.getPlateType())){
            throw new AlreadyExistsException("Plate already exists for this press size and type");
        }

        Plate plate = plateRepository.save(plateMapper.reqToEntity(plateRequest));
        return plateMapper.entityToResp(plate);
    }
}