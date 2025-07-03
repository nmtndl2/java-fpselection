package com.task.service.product.impl;

import com.task.dto.output.response.PressDataResponse;
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

        press.setPressSize(getOrDefault(pressRequest.getPressSize(), press.getPressSize()));
        press.setMaxChamber(getOrDefault(pressRequest.getMaxChamber(), press.getMaxChamber()));
        press.setCakeAirT(getOrDefault(pressRequest.getCakeAirT(), press.getCakeAirT()));
        press.setCyFwdT(getOrDefault(pressRequest.getCyFwdT(), press.getCyFwdT()));
        press.setCyRevT(getOrDefault(pressRequest.getCyRevT(), press.getCyRevT()));
        press.setDtAvailable(getOrDefault(pressRequest.getDtAvailable(), press.getDtAvailable()));
        press.setDtOpenT(getOrDefault(pressRequest.getDtOpenT(), press.getDtOpenT()));
        press.setDtClosedT(getOrDefault(pressRequest.getDtClosedT(), press.getDtClosedT()));
        press.setPsAvailable(getOrDefault(pressRequest.getPsAvailable(), press.getPsAvailable()));
        press.setPsFwdFPlateT(
                getOrDefault(pressRequest.getPsFwdFPlateT(), press.getPsFwdFPlateT()));
        press.setPsFwdT(getOrDefault(pressRequest.getPsFwdT(), press.getPsFwdT()));
        press.setPsFwdDT(getOrDefault(pressRequest.getPsFwdDT(), press.getPsFwdDT()));
        press.setPsRevT(getOrDefault(pressRequest.getPsRevT(), press.getPsRevT()));
        press.setPsRevDT(getOrDefault(pressRequest.getPsRevDT(), press.getPsRevDT()));
        press.setCwAvailable(getOrDefault(pressRequest.getCwAvailable(), press.getCwAvailable()));
        press.setCwFwdT(getOrDefault(pressRequest.getCwFwdT(), press.getCwFwdT()));
        press.setCwFwdDT(getOrDefault(pressRequest.getCwFwdDT(), press.getCwFwdDT()));
        press.setCwRevT(getOrDefault(pressRequest.getCwRevT(), press.getCwRevT()));
        press.setCwRevDT(getOrDefault(pressRequest.getCwRevDT(), press.getCwRevDT()));
        press.setCwDownT(getOrDefault(pressRequest.getCwDownT(), press.getCwDownT()));
        press.setCwDownDT(getOrDefault(pressRequest.getCwDownDT(), press.getCwDownDT()));
        press.setCwUpT(getOrDefault(pressRequest.getCwUpT(), press.getCwUpT()));
        press.setCwUpDT(getOrDefault(pressRequest.getCwUpDT(), press.getCwUpDT()));
        press.setCwFlowRate(getOrDefault(pressRequest.getCwFlowRate(), press.getCwFlowRate()));

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

    private <T> T getOrDefault(T newValue, T existingValue) {
        return newValue != null ? newValue : existingValue;
    }

    public int calculateChamber(int totalWetCake, int totalBatch, int onePlateVolume) {
        if (totalBatch == 0 || onePlateVolume == 0) {
            throw new IllegalArgumentException("Batch count and plate volume must not be zero");
        }
        double chambers = (double) totalWetCake / totalBatch / onePlateVolume;
        return roundUpToEven(chambers);
    }

    public int roundUpToEven(double value) {
        int ceil = (int) Math.ceil(value);
        return (ceil % 2 == 0) ? ceil : ceil + 1;
    }

    public int cwWaterUse(String pressSize) {

        Press press = getPressOrThrow(pressSize);
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

    public LocalTime calculatePressingTime(String pressSize, int dtCloseDT) {
        Press press = getPressOrThrow(pressSize);
        int totalSeconds = dtCloseDT + press.getCyFwdT().toSecondOfDay();
        return LocalTime.MIDNIGHT.plusSeconds(totalSeconds);
    }

    public LocalTime calculateOnePlatePsTime(String pressSize) {
        Press press = getPressOrThrow(pressSize);

        if (press.getPsFwdT() == null
                || press.getPsFwdDT() == null
                || press.getPsRevT() == null
                || press.getPsRevDT() == null) {
            throw new IllegalArgumentException("Missing press or plate shifting time data");
        }

        long onePlatePsTimeSecond =
                press.getPsFwdT().toSecondOfDay()
                        + press.getPsFwdDT().toSecondOfDay()
                        + press.getPsRevT().toSecondOfDay()
                        + press.getPsRevDT().toSecondOfDay();
        Duration onePlatePsTime = Duration.ofSeconds(onePlatePsTimeSecond);
        return LocalTime.MIDNIGHT.plus(onePlatePsTime);
    }

    public LocalTime calculateOneCyclePsTime(
            String pressSize, PressDataResponse pressData, LocalTime onePlatePsT) {
        Press press = getPressOrThrow(pressSize);
        long onCyclePsTimeSecond =
                (press.getCyRevT().toSecondOfDay()
                                + press.getDtOpenT().toSecondOfDay()
                                + (long) onePlatePsT.toSecondOfDay() * pressData.getNoOfChamber())
                        + ((long) press.getPsFwdT().toSecondOfDay()
                                * (pressData.getNoOfChamber() + 2));
        Duration onCyclePsTime = Duration.ofSeconds(onCyclePsTimeSecond);
        return LocalTime.MIDNIGHT.plus(onCyclePsTime);
    }

    public LocalTime calculateOnePlateCwTime(String pressSize) {
        Press press = getPressOrThrow(pressSize);
        long onePlateCwTimeSecond =
                press.getPsFwdT().toSecondOfDay()
                        + press.getPsFwdDT().toSecondOfDay()
                        + (press.getPsRevT().toSecondOfDay() / 2)
                        + press.getPsRevDT().toSecondOfDay()
                        + press.getCwDownT().toSecondOfDay()
                        + press.getCwDownDT().toSecondOfDay()
                        + press.getCwFwdT().toSecondOfDay()
                        + press.getCwFwdDT().toSecondOfDay()
                        + press.getCwRevT().toSecondOfDay()
                        + press.getCwRevDT().toSecondOfDay()
                        + press.getCwUpT().toSecondOfDay()
                        + press.getCwUpDT().toSecondOfDay()
                        + (press.getPsRevT().toSecondOfDay() / 2)
                        + press.getPsRevDT().toSecondOfDay();
        Duration onePlateCwTime = Duration.ofSeconds(onePlateCwTimeSecond);
        return LocalTime.MIDNIGHT.plus(onePlateCwTime);
    }

    public LocalTime calculateOneCycleCwTime(
            String pressSize, int noOfPress, LocalTime onePlateCwT) {
        Press press = getPressOrThrow(pressSize);
        long onCycleCwTimeSecond =
                press.getCyFwdT().toSecondOfDay()
                        + press.getCyRevT().toSecondOfDay()
                        + press.getDtClosedT().toSecondOfDay()
                        + ((long) onePlateCwT.toSecondOfDay() * noOfPress)
                        + ((long) press.getPsFwdT().toSecondOfDay() * (noOfPress + 2));
        Duration onCycleCwTime = Duration.ofSeconds(onCycleCwTimeSecond);
        return LocalTime.MIDNIGHT.plus(onCycleCwTime);
    }

    private Press getPressOrThrow(String pressSize) {
        Press press = pressRepository.findByPressSize(pressSize);
        if (press == null) {
            throw new ResourceNotExistsException("Press size '" + pressSize + "' not found");
        }
        return press;
    }
}
