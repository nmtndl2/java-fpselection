package com.task.service.product.impl;

import com.task.dto.product.request.PressRequest;
import com.task.dto.response.PressResponse;
import com.task.entities.product.Press;
import com.task.exception.BadRequestException;
import com.task.exception.ResourceNotExistsException;
import com.task.mapper.PressMapper;
import com.task.repository.product.PlateRepository;
import com.task.repository.product.PressRepository;
import com.task.service.product.PressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Slf4j
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

        if (pressRequest.getCwAvailable()) {
            if (!(pressRequest.getPsAvailable() && pressRequest.getDtAvailable())) {
                throw new IllegalArgumentException(
                        "When CW is available, both PS and DT must also be available.");
            }
        }

        Press press = pressRepository.save(pressMapper.reqToEntity(pressRequest));
        return pressMapper.entityToRes(press);
    }

    @Override
    public List<PressResponse> getAll() {
        List<Press> pressList = pressRepository.findAll();
        return pressMapper.entityToRes(pressList);
    }

    @Override
    public PressResponse updatePress(Long pressId, PressRequest pressRequest) {
        Press press =
                pressRepository
                        .findById(pressId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotExistsException(
                                                "Press id : " + pressId + " is not found !!"));

        press.setPressSize(
                (pressRequest.getPressSize() == null)
                        ? press.getPressSize()
                        : pressRequest.getPressSize());
        press.setMaxChamber(
                (pressRequest.getMaxChamber() == null)
                        ? press.getMaxChamber()
                        : pressRequest.getMaxChamber());
        press.setCakeAirT(
                (pressRequest.getCakeAirT() == null)
                        ? press.getCakeAirT()
                        : pressRequest.getCakeAirT());
        press.setCyFwdT(
                (pressRequest.getCyFwdT() == null) ? press.getCyFwdT() : pressRequest.getCyFwdT());
        press.setCyRevT(
                (pressRequest.getCyRevT() == null) ? press.getCyRevT() : pressRequest.getCyRevT());
        press.setDtAvailable(
                (pressRequest.getDtAvailable() == null)
                        ? press.getDtAvailable()
                        : pressRequest.getDtAvailable());
        press.setDtOpenT(
                (pressRequest.getDtOpenT() == null)
                        ? press.getDtOpenT()
                        : pressRequest.getDtOpenT());
        press.setDtClosedT(
                (pressRequest.getDtClosedT() == null)
                        ? press.getDtClosedT()
                        : pressRequest.getDtClosedT());
        press.setPsAvailable(
                (pressRequest.getPsAvailable() == null)
                        ? press.getPsAvailable()
                        : pressRequest.getPsAvailable());
        press.setPsFwdFPlateT(
                (pressRequest.getPsFwdFPlateT() == null)
                        ? press.getPsFwdFPlateT()
                        : pressRequest.getPsFwdFPlateT());
        press.setPsFwdT(
                (pressRequest.getPsFwdT() == null) ? press.getPsFwdT() : pressRequest.getPsFwdT());
        press.setPsFwdDT(
                (pressRequest.getPsFwdDT() == null)
                        ? press.getPsFwdDT()
                        : pressRequest.getPsFwdDT());
        press.setPsRevT(
                (pressRequest.getPsRevT() == null) ? press.getPsRevT() : pressRequest.getPsRevT());
        press.setPsRevDT(
                (pressRequest.getPsRevDT() == null)
                        ? press.getPsRevDT()
                        : pressRequest.getPsRevDT());
        press.setCwAvailable(
                (pressRequest.getCwAvailable() == null)
                        ? press.getCwAvailable()
                        : pressRequest.getCwAvailable());
        press.setCwFwdT(
                (pressRequest.getCwFwdT() == null) ? press.getCwFwdT() : pressRequest.getCwFwdT());
        press.setCwFwdDT(
                (pressRequest.getCwFwdDT() == null)
                        ? press.getCwFwdDT()
                        : pressRequest.getCwFwdDT());
        press.setCwRevT(
                (pressRequest.getCwRevT() == null) ? press.getCwRevT() : pressRequest.getCwRevT());
        press.setCwRevDT(
                (pressRequest.getCwRevDT() == null)
                        ? press.getCwRevDT()
                        : pressRequest.getCwRevDT());
        press.setCwDownT(
                (pressRequest.getCwDownT() == null)
                        ? press.getCwDownT()
                        : pressRequest.getCwDownT());
        press.setCwDownDT(
                (pressRequest.getCwDownDT() == null)
                        ? press.getCwDownDT()
                        : pressRequest.getCwDownDT());
        press.setCwUpT(
                (pressRequest.getCwUpT() == null) ? press.getCwUpT() : pressRequest.getCwUpT());
        press.setCwUpDT(
                (pressRequest.getCwUpDT() == null) ? press.getCwUpDT() : pressRequest.getCwUpDT());
        press.setCwFlowRate(
                (pressRequest.getCwFlowRate() == null)
                        ? press.getCwFlowRate()
                        : pressRequest.getCwFlowRate());

        Press updatePress = pressRepository.save(press);

        return pressMapper.entityToRes(updatePress);
    }

    @Override
    public void deletePressById(Long pressId) {
        boolean exists = pressRepository.existsById(pressId);
        if (!exists) {
            throw new BadRequestException("Press with press size " + pressId + " does not exist.");
        }
        pressRepository.deleteById(pressId);
    }

    @Override
    public PressResponse getPress(Long pressId) {
        Press press =
                pressRepository
                        .findById(pressId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotExistsException(
                                                "Press id : " + pressId + " is not found !!"));

        return pressMapper.entityToRes(press);
    }

    public int calculateChamber(int totalWetCake, int totalBatch, int onePlateVolume) {
        double chambers = (double) totalWetCake / totalBatch / onePlateVolume;
        return roundUpToEven(chambers);
    }

    public int roundUpToEven(double value) {
        int ceil = (int) Math.ceil(value);
        return (ceil % 2 == 0) ? ceil : ceil + 1;
    }

    public int cwWaterUse(String pressSize) {

        Press press = pressRepository.findByPressSize(pressSize);
        long pumpOnSeconds =
                (long) press.getCwFwdT().toSecondOfDay()
                        + press.getCwFwdDT().toSecondOfDay()
                        + press.getCwRevT().toSecondOfDay();

        double flowRateLitersPerSecond = ((press.getCwFlowRate() / 1.5) * 1000.0) / 3600.0;

        return (int) (flowRateLitersPerSecond * pumpOnSeconds);
    }

    public int calculateDtCTime(String pressSize, boolean dripTray) {
        if (dripTray) {
            return pressRepository.findByPressSize(pressSize).getDtClosedT().toSecondOfDay();
        }
        return 0;
    }

    public int calculateDtOTime(String pressSize, boolean dripTray) {
        if (dripTray) {
            return pressRepository.findByPressSize(pressSize).getDtClosedT().toSecondOfDay();
        }
        return 0;
    }

    public LocalTime calculatePressingTime(String pressSize, int dtCloseDT) {
        Press press = pressRepository.findByPressSize(pressSize);
        int totalSeconds = dtCloseDT + press.getCyFwdT().toSecondOfDay();
        return LocalTime.MIDNIGHT.plusSeconds(totalSeconds);
    }
}
