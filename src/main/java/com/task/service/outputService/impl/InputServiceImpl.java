package com.task.service.outputService.impl;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.outputResponse.DashboardResponse;
import com.task.dto.outputResponse.PressDataResponse;
import com.task.dto.outputResponse.PressTResponse;
import com.task.dto.outputResponse.SlurryResponse;
import com.task.entities.product.*;
import com.task.mapper.InputMapper;
import com.task.repository.product.*;
import com.task.service.outputService.InputService;
import com.task.service.product.impl.FeedPumpServiceImpl;
import com.task.service.product.impl.PressServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InputServiceImpl implements InputService {

    private final PressRepository pressRepository;
    private final PlateRepository plateRepository;
    private final SqPumpRepository sqPumpRepository;
    private final FeedPumpRepository feedPumpRepository;
    private final InputRepository inputRepository;
    private final InputMapper inputMapper;

    private final PressServiceImpl pressService;
    private final FeedPumpServiceImpl feedPumpService;

    private static final double AIR_COMPRESS_DIVISOR = 28.28;
    private static final LocalTime SQ_OUTLET_DEFAULT_TIME = LocalTime.parse("00:20:00");
    private static final String MEMBRANE_PLATE_TYPE = "Membrane";

    @Override
    @Transactional
    public DashboardResponse calculateDashboardData(InputRequest inputRequest) {
        List<String> pressSizes = inputRequest.getPressSizes();
        List<String> warnings = new ArrayList<>();
        List<PressDataResponse> responseList = new ArrayList<>();
        List<PressTResponse> pressTResponses = new ArrayList<>();

        String plateType = inputRequest.getPlateType();
        SlurryResponse slurryResponse = calculateSlurryResponse(inputRequest);

        for (String pressSize : pressSizes) {
            log.info("Processing pressSize: {}", pressSize);

            if (!validateEntities(pressSize, plateType, warnings)) {
                log.info("Skipping pressSize: {} due to missing data", pressSize);
                continue;
            }

            Press press = pressRepository.findByPressSize(pressSize);
            Plate plate = plateRepository.findByPressSizeAndPlateType(pressSize, plateType);
            SqPump sqPump = sqPumpRepository.findByPressSize(pressSize);

            PressDataResponse pressData =
                    buildPressDataResponse(
                            inputRequest, press, plate, sqPump, slurryResponse, plateType);
            responseList.add(pressData);
        }

        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setPressDataResponse(responseList);
        dashboardResponse.setPressTResponse(pressTResponses);
        dashboardResponse.setSlurryResponse(slurryResponse);
        dashboardResponse.setWarnings(warnings);

        inputRepository.save(inputMapper.reqToEntity(inputRequest));
        return dashboardResponse;
    }

    private boolean validateEntities(String pressSize, String plateType, List<String> warnings) {
        boolean valid = true;

        if (pressRepository.findByPressSize(pressSize) == null) {
            warnings.add("Press not found for size " + pressSize);
            valid = false;
        }

        if (plateRepository.findByPressSizeAndPlateType(pressSize, plateType) == null) {
            warnings.add("Plate not found for size " + pressSize + " and type " + plateType);
            valid = false;
        }

        if (MEMBRANE_PLATE_TYPE.equalsIgnoreCase(plateType)
                && sqPumpRepository.findByPressSize(pressSize) == null) {
            warnings.add("SQ Pump not found for size " + pressSize);
            valid = false;
        }

        return valid;
    }

    private SlurryResponse calculateSlurryResponse(InputRequest inputRequest) {
        double density =
                inputRequest.getDensityOfDrySolid() != null
                        ? inputRequest.getDensityOfDrySolid()
                        : 1;
        int drySolid =
                (int)
                        ((inputRequest.getSludgeQty()
                                        * 1000
                                        * inputRequest.getDrySolidParticle()
                                        / 100)
                                / density);
        int wetCake = (int) (drySolid * (100 / (100 - inputRequest.getMoistureContain())));

        SlurryResponse response = new SlurryResponse();
        response.setTotalDrySolid(drySolid);
        response.setTotalWetCake(wetCake);
        return response;
    }

    private PressDataResponse buildPressDataResponse(
            InputRequest inputRequest,
            Press press,
            Plate plate,
            SqPump sqPump,
            SlurryResponse slurryResponse,
            String plateType) {
        int onePlateVolume = plate.getVolume();
        int noOfPress = (inputRequest.getNoOfPress() == 0) ? 1 : inputRequest.getNoOfPress();
        int noOfBatch = (inputRequest.getNoOfBatch() == 0) ? 1 : inputRequest.getNoOfBatch();
        int totalBatch = noOfPress * noOfBatch;
        int noOfChamber =
                pressService.calculateChamber(
                        slurryResponse.getTotalWetCake(), totalBatch, onePlateVolume);
        int totalVolume = noOfChamber * onePlateVolume;
        int customFlowRate = inputRequest.getCusFeedRate();
        int getMaxChamber = press.getMaxChamber();

        int feedPumpFlow =
                feedPumpService.calculateFeedPump(
                        press.getPressSize(), noOfChamber, getMaxChamber, customFlowRate);
        int airCompressDeli = (int) Math.ceil(totalVolume / AIR_COMPRESS_DIVISOR);

        Integer sqWaterUsed = null;
        Double sqFlowRate = null;
        Double selectedFlowRate = null;
        Integer sqTankCap = null;

        if (MEMBRANE_PLATE_TYPE.equalsIgnoreCase(plateType)) {
            int sqInletWater = sqPump.getSqInletWater();
            sqWaterUsed = sqInletWater * (noOfChamber / 2);

            List<Double> flowRates =
                    sqPump.getFlowRates().stream().map(SqCalcFR::getFlowRate).sorted().toList();
            for (Double rate : flowRates) {
                if ((sqWaterUsed / (rate * 1000 / 60)) <= sqPump.getSqMaxTMin()) {
                    selectedFlowRate = rate;
                    break;
                }
            }

            sqFlowRate = (noOfChamber > press.getMaxChamber() && selectedFlowRate != null) ? selectedFlowRate : 0.0;
            int calcSqTankCap = sqWaterUsed + (int) (sqWaterUsed * 0.3);
            sqTankCap = roundUpToNextHundred(calcSqTankCap);
        }

        long pumpOnSeconds =
                press.getCwFwdT().toSecondOfDay()
                        + press.getCwFwdDT().toSecondOfDay()
                        + press.getCwRevT().toSecondOfDay();
        double flowRateLitersPerSecond = ((press.getCwFlowRate() / 1.5) * 1000.0) / 3600.0;

        int cw1PWaterUsed = (int) (flowRateLitersPerSecond * pumpOnSeconds);
        int cw1CWaterUsed = cw1PWaterUsed * noOfChamber;
        int calcCwTankCap = cw1CWaterUsed + (int) (cw1CWaterUsed * 0.3);
        int cwTankCap = roundUpToNextHundred(calcCwTankCap);

        boolean isMembrane = MEMBRANE_PLATE_TYPE.equalsIgnoreCase(plateType);
        boolean isClothWashing = inputRequest.isClothWashing();

        PressDataResponse pressData = new PressDataResponse();
        pressData.setPressSize(press.getPressSize());
        pressData.setPlateType(plateType);
        pressData.setOnePlateVolume(onePlateVolume);
        pressData.setNoOfPress(noOfPress);
        pressData.setNoOfBatch(noOfBatch);
        pressData.setNoOfChamber(noOfChamber);
        pressData.setTotalVolume(totalVolume);
        pressData.setFeedPumpFlow(feedPumpFlow);
        pressData.setAirCompressDeli(isMembrane ? null : airCompressDeli);
        pressData.setSqFlowRate(isMembrane ? sqFlowRate : null);
        pressData.setSqWaterUsed(isMembrane ? sqWaterUsed : null);
        pressData.setSqTankCap(isMembrane ? sqTankCap : null);

        pressData.setCw1PWaterUsed(isClothWashing ? cw1PWaterUsed : null);
        pressData.setCw1CWaterUsed(isClothWashing ? cw1CWaterUsed : null);
        pressData.setCwTankCap(isClothWashing ? cwTankCap : null);

        if (noOfChamber > press.getMaxChamber()) {
            String msg =
                    String.format(
                            "Calculated chambers (%d) exceed max (%d).",
                            noOfChamber, press.getMaxChamber());
            log.warn(msg);
            pressData.setMessage(msg);
            pressData.setTotalVolume(null);
            pressData.setFeedPumpFlow(null);
            pressData.setAirCompressDeli(null);
            pressData.setCw1PWaterUsed(null);
            pressData.setCw1CWaterUsed(null);
            pressData.setCwTankCap(null);
            pressData.setSqFlowRate(null);
            pressData.setSqWaterUsed(null);
            pressData.setSqTankCap(null);
        }

        return pressData;
    }

    public static int roundUpToNextHundred(int number) {
        return (number % 100 == 0) ? number : ((number + 99) / 100) * 100;
    }

}
