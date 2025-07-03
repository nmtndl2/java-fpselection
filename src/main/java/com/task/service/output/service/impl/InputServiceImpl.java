package com.task.service.output.service.impl;

import com.task.dto.input.request.InputRequest;
import com.task.dto.output.response.DashboardResponse;
import com.task.dto.output.response.PressDataResponse;
import com.task.dto.output.response.PressTResponse;
import com.task.dto.output.response.SlurryResponse;
import com.task.entities.product.*;
import com.task.mapper.InputMapper;
import com.task.repository.product.*;
import com.task.service.output.service.InputService;
import com.task.service.product.impl.FeedPumpServiceImpl;
import com.task.service.product.impl.PressServiceImpl;
import com.task.service.product.impl.SqPumpServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final InputRepository inputRepository;
    private final InputMapper inputMapper;

    private final PressServiceImpl pressService;
    private final FeedPumpServiceImpl feedPumpService;
    private final SqPumpServiceImpl sqPumpService;

    private static final double AIR_COMPRESS_DIVISOR = 28.28;
    private static final LocalTime SQ_OUTLET_DEFAULT_TIME = LocalTime.parse("00:20:00");
    private static final String MEMBRANE_PLATE_TYPE = "Membrane";
    private static final int DEFAULT_PRESS = 1;
    private static final int DEFAULT_BATCH = 1;

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

            PressDataResponse pressData =
                    buildPressDataResponse(inputRequest, press, plate, slurryResponse, plateType);
            responseList.add(pressData);

            PressTResponse pressDataTime = buildPressDataTime(inputRequest, press, pressData);
            pressTResponses.add(pressDataTime);
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
            SlurryResponse slurryResponse,
            String plateType) {
        int onePlateVolume = plate.getVolume();
        int noOfPress =
                (inputRequest.getNoOfPress() == 0) ? DEFAULT_PRESS : inputRequest.getNoOfPress();
        int noOfBatch =
                (inputRequest.getNoOfBatch() == 0) ? DEFAULT_BATCH : inputRequest.getNoOfBatch();
        int totalBatch = noOfPress * noOfBatch;
        int noOfChamber =
                pressService.calculateChamber(
                        slurryResponse.getTotalWetCake(), totalBatch, onePlateVolume);
        int totalVolume = noOfChamber * onePlateVolume;
        int getMaxChamber = press.getMaxChamber();

        Integer feedPumpFlow = null;
        Integer airCompressDeli = null;

        Integer sqWaterUsed = null;
        Double sqFlowRate = null;
        Integer sqTankCap = null;

        Integer cw1PWaterUsed = null;
        Integer cw1CWaterUsed = null;
        Integer cwTankCap = null;

        boolean isValidChamber = noOfChamber <= getMaxChamber;
        boolean isMembrane = MEMBRANE_PLATE_TYPE.equalsIgnoreCase(plateType);
        boolean isClothWashing = inputRequest.isClothWashing();

        if (isValidChamber) {
            feedPumpFlow =
                    feedPumpService.calculateFeedPump(
                            press.getPressSize(), noOfChamber, inputRequest.getCusFeedRate());

            airCompressDeli = (int) Math.ceil(totalVolume / AIR_COMPRESS_DIVISOR);

            if (isMembrane) {

                int sqInletWater = sqPumpService.calculateSqInletWater(press.getPressSize());

                sqWaterUsed = sqInletWater * (noOfChamber / 2);

                sqFlowRate = sqPumpService.calculateSqFlowRate(press.getPressSize(), sqWaterUsed);

                int calcSqTankCap = sqWaterUsed + (int) (sqWaterUsed * 0.3);
                sqTankCap = roundUpToNextHundred(calcSqTankCap);
            }

            if (isClothWashing) {
                cw1PWaterUsed = pressService.cwWaterUse(press.getPressSize());
                cw1CWaterUsed = cw1PWaterUsed * noOfChamber;

                int calcCwTankCap = cw1CWaterUsed + (int) (cw1CWaterUsed * 0.3);
                cwTankCap = roundUpToNextHundred(calcCwTankCap);
            }
        }

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

        if (!isValidChamber) {
            handleExceededChamberLimit(noOfChamber, getMaxChamber, pressData);
        }

        return pressData;
    }

    private PressTResponse buildPressDataTime(
            InputRequest inputRequest, Press press, PressDataResponse pressData) {
        // Pressing Cycle Time (Duration)
        boolean isValidChamber = pressData.getNoOfChamber() <= press.getMaxChamber();

        String plateType = pressData.getPlateType();
        String pressSize = pressData.getPressSize();

        PressTResponse pressTimeResponse = new PressTResponse();

        if (isValidChamber) {

            int dtCloseDT = pressService.calculateDtCTime(pressSize, inputRequest.isDripTray());
            int dtOpenDT = pressService.calculateDtOTime(pressSize, inputRequest.isDripTray());

            LocalTime pressingT = pressService.calculatePressingTime(pressSize, dtCloseDT);

            // Feed Time (Duration)
            LocalTime feedT = feedPumpService.calculateFeedTime(pressData, inputRequest.getSludgeQty());

            // Cake Washing Time (Duration)
            LocalTime cakeWT = (inputRequest.isCakeWashing()) ? inputRequest.getWashingT() : null;

            // Air Time (Duration)
            LocalTime cakeAirT = press.getCakeAirT();

            // Squeezing Time (Duration)
            Duration sqInletTime;
            LocalTime sqInletT;
            LocalTime sqOutletT;
            if ("Membrane".equalsIgnoreCase(plateType)) {
                long sqInletTimeSecond =
                        (pressData.getSqFlowRate() != 0)
                                ? (long)
                                        ((pressData.getSqWaterUsed())
                                                / (pressData.getSqFlowRate() * 1000 / 3600))
                                : 0;
                sqInletTime = Duration.ofSeconds(sqInletTimeSecond);
                sqInletT = LocalTime.MIDNIGHT.plus(sqInletTime);

                sqOutletT =
                        (inputRequest.getSqOutletT() == null)
                                ? SQ_OUTLET_DEFAULT_TIME
                                : inputRequest.getSqOutletT();
            } else {
                sqInletT = null;
                sqOutletT = null;
            }

            LocalTime onePlatePsT;
            LocalTime onCyclePsT;
            if (press.getPsAvailable()) {
                // Plate Shifter Time (Duration)
                long onePlatePsTimeSecond =
                        press.getPsFwdT().toSecondOfDay()
                                + press.getPsFwdDT().toSecondOfDay()
                                + press.getPsRevT().toSecondOfDay()
                                + press.getPsRevDT().toSecondOfDay();
                Duration onePlatePsTime = Duration.ofSeconds(onePlatePsTimeSecond);
                onePlatePsT = LocalTime.MIDNIGHT.plus(onePlatePsTime);

                long onCyclePsTimeSecond =
                        (press.getCyRevT().toSecondOfDay()
                                        + dtOpenDT
                                        + (long) onePlatePsT.toSecondOfDay()
                                                * pressData.getNoOfChamber())
                                + ((long) press.getPsFwdT().toSecondOfDay()
                                        * (pressData.getNoOfChamber() + 2));
                Duration onCyclePsTime = Duration.ofSeconds(onCyclePsTimeSecond);
                onCyclePsT = LocalTime.MIDNIGHT.plus(onCyclePsTime);
            } else {
                onePlatePsT = null;
                onCyclePsT = null;
            }

            LocalTime onePlateCwT;
            LocalTime onCycleCwT;
            if (press.getCwAvailable()) {

                // ClothWashing Time (Duration)
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
                onePlateCwT = LocalTime.MIDNIGHT.plus(onePlateCwTime);

                long onCycleCwTimeSecond =
                        press.getCyFwdT().toSecondOfDay()
                                + press.getCyRevT().toSecondOfDay()
                                + dtCloseDT
                                + ((long) onePlateCwT.toSecondOfDay() * pressData.getNoOfChamber())
                                + ((long) press.getPsFwdT().toSecondOfDay()
                                        * (pressData.getNoOfChamber() + 2));
                Duration onCycleCwTime = Duration.ofSeconds(onCycleCwTimeSecond);
                onCycleCwT = LocalTime.MIDNIGHT.plus(onCycleCwTime);
            } else {
                onePlateCwT = null;
                onCycleCwT = null;
            }

            pressTimeResponse.setPressingCT(pressingT);
            pressTimeResponse.setFeedT(feedT);
            pressTimeResponse.setCakeAirT(cakeAirT);
            pressTimeResponse.setSqInletT(sqInletT);
            pressTimeResponse.setSqOutletT(sqOutletT);
            pressTimeResponse.setOnePlatePsT(onePlatePsT);
            pressTimeResponse.setOnCyclePsT(onCyclePsT);
            pressTimeResponse.setOnePlateCwT(onePlateCwT);
            pressTimeResponse.setOnCycleCwT(onCycleCwT);
            pressTimeResponse.setCakeWT(cakeWT);
        }

        if (pressData.getNoOfChamber() > press.getMaxChamber()) {
            String msg =
                    String.format(
                            "Calculated number of chambers (%d) exceeds the press's maximum capacity (%d).",
                            pressData.getNoOfChamber(), press.getMaxChamber());
            pressTimeResponse.setMessage(msg);
            pressTimeResponse.setPressingCT(null);
            pressTimeResponse.setFeedT(null);
            pressTimeResponse.setCakeAirT(null);
            pressTimeResponse.setSqInletT(null);
            pressTimeResponse.setSqOutletT(null);
            pressTimeResponse.setOnePlatePsT(null);
            pressTimeResponse.setOnCyclePsT(null);
            pressTimeResponse.setOnePlateCwT(null);
            pressTimeResponse.setOnCycleCwT(null);
            pressTimeResponse.setCakeWT(null);
        }

        if ("Membrane".equalsIgnoreCase(plateType)) {
            pressTimeResponse.setCakeAirT(null);
        } else {
            pressTimeResponse.setSqInletT(null);
            pressTimeResponse.setSqOutletT(null);
        }

        if (!inputRequest.isClothWashing()) {
            pressTimeResponse.setOnePlateCwT(null);
            pressTimeResponse.setOnCycleCwT(null);
            pressTimeResponse.setCakeWT(null);
        }

        return pressTimeResponse;
    }

    private void handleExceededChamberLimit(
            int noOfChamber, int maxChamber, PressDataResponse pressData) {
        String msg =
                String.format("Calculated chambers (%d) exceed max (%d).", noOfChamber, maxChamber);
        log.warn(msg);
        pressData.setMessage(msg);
        pressData.setTotalVolume(null);
        pressData.setFeedPumpFlow(null);
        pressData.setAirCompressDeli(null);
        pressData.setSqFlowRate(null);
        pressData.setSqWaterUsed(null);
        pressData.setSqTankCap(null);
        pressData.setCw1PWaterUsed(null);
        pressData.setCw1CWaterUsed(null);
        pressData.setCwTankCap(null);
    }

    public static int roundUpToNextHundred(int number) {
        return (number % 100 == 0) ? number : ((number + 99) / 100) * 100;
    }
}
