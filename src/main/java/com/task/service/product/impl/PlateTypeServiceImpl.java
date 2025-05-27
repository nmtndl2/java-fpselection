package com.task.service.product.impl;

import com.task.dto.productRequest.PlateTypeRequest;
import com.task.dto.response.PlateResponse;
import com.task.dto.response.PlateTypeResponse;
import com.task.entities.product.Plate;
import com.task.entities.product.PlateType;
import com.task.exception.AlreadyExistsException;
import com.task.exception.BadRequestException;
import com.task.exception.ResourceNotExistsException;
import com.task.mapper.PlateTypeMapper;
import com.task.repository.product.PlateRepository;
import com.task.repository.product.PlateTypeRepository;
import com.task.service.product.PlateTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlateTypeServiceImpl implements PlateTypeService {

    private final PlateTypeRepository plateTypeRepository;
    private final PlateTypeMapper plateTypeMapper;
    private final PlateRepository plateRepository;

    @Override
    public PlateTypeResponse addPlateType(PlateTypeRequest plateTypeRequest) {
        if (plateTypeRepository.existsByTypeName(plateTypeRequest.getTypeName())) {
            throw new AlreadyExistsException("Plate Type already exists");
        }

        PlateType plateType = plateTypeRepository.save(plateTypeMapper.reqToEntity(plateTypeRequest));
        return plateTypeMapper.entityToResp(plateType);
    }

    @Override
    public String deletePlateType(Long plateTypeId) {
        PlateType plateType = plateTypeRepository.findById(plateTypeId)
                .orElseThrow(() -> new ResourceNotExistsException(
                        "Plate type with ID " + plateTypeId + " does not exist"
                ));

        boolean usedPlateType = plateRepository.existsByPlateType(plateType.getTypeName());

        if (usedPlateType) {
            throw new BadRequestException("Plate Type Used in plate");
        }

        plateTypeRepository.delete(plateType);
        return "Plate type successfully deleted";
    }

    @Override
    public List<PlateType> findAll() {
        return plateTypeRepository.findAll();
    }

    @Override
    public List<String> getMemPlateSize() {

        String plateType = "Membrane";

        List<String> memPlateSizeList = plateRepository.findAll().stream()
                .filter(plate -> plateType.equalsIgnoreCase(plate.getPlateType()))
                .map(Plate::getPressSize)
                .distinct()
                .sorted(Comparator.comparingInt(this::getArea))
                .toList();

        return memPlateSizeList;
    }

    @Override
    public List<PlateTypeResponse> getAll() {
        List<PlateType> plateTypes = plateTypeRepository.findAll();
        List<PlateTypeResponse> plateTypeResponses = plateTypeMapper.entityToResp(plateTypes);
        return plateTypeResponses;
    }

    private int getArea(String size) {
        try {
            String[] parts = size.toLowerCase().split("x");
            int width = Integer.parseInt(parts[0].trim());
            int height = Integer.parseInt(parts[1].trim());
            return width * height;
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
