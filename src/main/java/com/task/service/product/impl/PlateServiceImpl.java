package com.task.service.product.impl;

import com.task.dto.product.request.PlateRequest;
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

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlateServiceImpl implements PlateService {

    private static final String PLATE_TYPE_NOT_FOUND = "Plate type not found";

    private final PlateRepository plateRepository;
    private final PlateTypeRepository plateTypeRepository;
    private final PlateMapper plateMapper;

    @Override
    public PlateResponse addPlate(PlateRequest plateRequest) {

        if (!plateTypeRepository.existsByTypeName(plateRequest.getPlateType())) {
            throw new ResourceNotExistsException(PLATE_TYPE_NOT_FOUND);
        }

        if (plateRepository.existsByPressSizeAndPlateType(
                plateRequest.getPressSize(), plateRequest.getPlateType())) {
            throw new AlreadyExistsException("Plate already exists for this press size and type");
        }

        Plate plate = plateRepository.save(plateMapper.reqToEntity(plateRequest));
        return plateMapper.entityToResp(plate);
    }

    @Override
    public PlateResponse updatePlate(Long plateId, PlateRequest plateRequest) {

        Plate plate =
                plateRepository
                        .findById(plateId)
                        .orElseThrow(() -> new ResourceNotExistsException(PLATE_TYPE_NOT_FOUND));

        plate.setPressSize(
                (plateRequest.getPressSize() == null)
                        ? plate.getPressSize()
                        : plateRequest.getPressSize());
        plate.setPlateType(
                (plateRequest.getPlateType() == null)
                        ? plate.getPlateType()
                        : plateRequest.getPlateType());
        plate.setVolume(
                (plateRequest.getVolume() == null) ? plate.getVolume() : plateRequest.getVolume());
        plate.setFiltrationArea(
                (plateRequest.getFiltrationArea() == null)
                        ? plate.getFiltrationArea()
                        : plateRequest.getFiltrationArea());
        plate.setCakeThk(
                (plateRequest.getCakeThk() == null)
                        ? plate.getCakeThk()
                        : plateRequest.getCakeThk());
        plate.setFinalCakeThk(
                (plateRequest.getFinalCakeThk() == null)
                        ? plate.getFinalCakeThk()
                        : plateRequest.getFinalCakeThk());

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

        Plate plate =
                plateRepository
                        .findById(plateId)
                        .orElseThrow(() -> new ResourceNotExistsException(PLATE_TYPE_NOT_FOUND));

        return plateMapper.entityToResp(plateRepository.save(plate));
    }

    @Override
    public String deleteById(Long plateId) {

        plateRepository
                .findById(plateId)
                .orElseThrow(() -> new ResourceNotExistsException(PLATE_TYPE_NOT_FOUND));

        plateRepository.deleteById(plateId);

        return "Plate successfully deleted";
    }

    @Override
    public List<String> getPressSizeByPlateType(String plateType) {
        return plateRepository.findAll().stream()
                .filter(plate -> plate.getPlateType().equalsIgnoreCase(plateType))
                .map(Plate::getPressSize)
                .distinct()
                .sorted(Comparator.comparingInt(this::getArea))
                .toList();
    }

    @Override
    public List<String> findAllPressSize() {
        return plateRepository.findAll().stream()
                .map(Plate::getPressSize)
                .distinct()
                .sorted(Comparator.comparingInt(this::getArea))
                .toList();
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
