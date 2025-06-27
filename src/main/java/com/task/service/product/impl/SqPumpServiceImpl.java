package com.task.service.product.impl;

import com.task.dto.product.request.SqPumpRequest;
import com.task.dto.response.SqPumpResponse;
import com.task.entities.product.SqCalcFR;
import com.task.entities.product.SqPump;
import com.task.exception.AlreadyExistsException;
import com.task.exception.ResourceNotExistsException;
import com.task.mapper.SqPumpMapper;
import com.task.repository.product.PlateRepository;
import com.task.repository.product.SqPumpRepository;
import com.task.service.product.SqPumpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SqPumpServiceImpl implements SqPumpService {

    private final SqPumpRepository sqPumpRepository;
    private final PlateRepository plateRepository;
    private final SqPumpMapper sqPumpMapper;

    private static final String SQ_PUMP_NOT_EXIST = "Sq Pump not exists";

    @Override
    public SqPumpResponse addSqPump(SqPumpRequest sqPumpRequest) {

        boolean existsByPressSize =
                sqPumpRepository.existsByPressSize(sqPumpRequest.getPressSize());
        if (existsByPressSize) {
            throw new AlreadyExistsException("Already press size exist");
        }

        String plateType = "Membrane";

        boolean isMembrane =
                plateRepository.existsByPressSizeAndPlateType(
                        sqPumpRequest.getPressSize(), plateType);

        if (!isMembrane) {
            throw new ResourceNotExistsException("Plate not found in plate table");
        }

        SqPump sqPump = sqPumpMapper.toEntity(sqPumpRequest);

        SqPump finalSqPump = sqPump;
        sqPump.getFlowRates().forEach(fr -> fr.setSqPump(finalSqPump));

        sqPump = sqPumpRepository.save(sqPump);

        return sqPumpMapper.entityToRes(sqPump);
    }

    @Override
    public List<SqPump> getAll() {
        List<SqPump> sqPumpList = sqPumpRepository.findAll();
        return sqPumpMapper.entityToRes(sqPumpList);
    }

    @Override
    public SqPumpResponse updateSqPump(Long id, SqPumpRequest sqPumpRequest) {
        SqPump sqPump =
                sqPumpRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotExistsException(SQ_PUMP_NOT_EXIST));

        sqPump.setPressSize(
                (sqPumpRequest.getPressSize() == null)
                        ? sqPump.getPressSize()
                        : sqPumpRequest.getPressSize());
        sqPump.setSqInletWater(
                (sqPumpRequest.getSqInletWater() == null)
                        ? sqPump.getSqInletWater()
                        : sqPumpRequest.getSqInletWater());
        sqPump.setSqMaxTMin(
                (sqPumpRequest.getSqMaxTMin() == null)
                        ? sqPump.getSqMaxTMin()
                        : sqPumpRequest.getSqMaxTMin());

        sqPump.getFlowRates().clear();

        sqPumpRequest
                .getFlowRates()
                .forEach(
                        flowRateReq -> {
                            SqCalcFR sqCalcFR = new SqCalcFR();
                            sqCalcFR.setFlowRate(flowRateReq.getFlowRate());
                            sqCalcFR.setSqPump(sqPump);
                            sqPump.getFlowRates().add(sqCalcFR);
                        });

        SqPump updateSqPump = sqPumpRepository.save(sqPump);

        return sqPumpMapper.entityToRes(updateSqPump);
    }

    @Override
    public SqPumpResponse getSqPump(Long id) {
        SqPump sqPump =
                sqPumpRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotExistsException(SQ_PUMP_NOT_EXIST));
        return sqPumpMapper.entityToRes(sqPump);
    }

    @Override
    public String deleteSqPump(Long id) {
        sqPumpRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotExistsException(SQ_PUMP_NOT_EXIST));

        sqPumpRepository.deleteById(id);
        return "Delete pump successfully";
    }

    public int calculateSqInletWater(String pressSize) {
        return sqPumpRepository.findByPressSize(pressSize).getSqInletWater();
    }

    public Double calculateSqFlowRate(String pressSize, int sqWaterUsed) {
        SqPump sqPump = sqPumpRepository.findByPressSize(pressSize);

        if (sqPump.getFlowRates() == null || sqPump.getFlowRates().isEmpty()) {
            return null;
        }

        List<Double> flowRates = sqPump.getFlowRates()
                .stream()
                .map(SqCalcFR::getFlowRate)
                .sorted()
                .toList();

        for (Double rate : flowRates) {
            if ((sqWaterUsed / (rate * 1000 / 60)) <= sqPump.getSqMaxTMin()) {
                return rate;
            }
        }

        return null;
    }

}
