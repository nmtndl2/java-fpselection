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

import java.util.List;
import java.util.Optional;

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

    @Override
    public PlateResponse updatePlate(Long plateId, PlateRequest plateRequest) {

        Plate plate = plateRepository.findById(plateId)
                .orElseThrow(() -> new ResourceNotExistsException("Plate type not found"));

        plate.setPressSize((plateRequest.getPressSize() == null) ? plate.getPressSize() : plateRequest.getPressSize());
        plate.setPlateType((plateRequest.getPlateType() == null) ? plate.getPlateType() : plateRequest.getPlateType());
        plate.setVolume((plateRequest.getVolume() == null) ? plate.getVolume() : plateRequest.getVolume());
        plate.setFiltrationArea((plateRequest.getFiltrationArea() == null) ? plate.getFiltrationArea() : plateRequest.getFiltrationArea());
        plate.setCakeThk((plateRequest.getCakeThk() == null) ? plate.getCakeThk() : plateRequest.getCakeThk());
        plate.setFinalCakeThk((plateRequest.getFinalCakeThk() == null) ? plate.getFinalCakeThk() : plateRequest.getFinalCakeThk());

        Plate updatePlate = plateRepository.save(plate);

        return plateMapper.entityToResp(plateRepository.save(updatePlate));
    }

    @Override
    public List<PlateResponse> getAll() {
        List<Plate> plateList = plateRepository.findAll();

        if (plateList.isEmpty()) {
            throw new ResourceNotExistsException("Plate list is empty!!");
        }

        return plateMapper.entityToResp(plateList);
    }

    @Override
    public PlateResponse getById(Long plateId) {

        Plate plate = plateRepository.findById(plateId)
                .orElseThrow(() -> new ResourceNotExistsException("Plate type not found"));

        return plateMapper.entityToResp(plateRepository.save(plate));
    }

    @Override
    public String deleteById(Long plateId) {

        Plate plate = plateRepository.findById(plateId)
                .orElseThrow(() -> new ResourceNotExistsException("Plate type not found"));

        plateRepository.deleteById(plateId);

        return "Plate successfully deleted";
    }

}