package com.task.service.outputService.impl;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.outputResponse.DashboardResponse;
import com.task.dto.outputResponse.PressDataResponse;
import com.task.dto.outputResponse.PressTResponse;
import com.task.dto.outputResponse.SlurryResponse;
import com.task.entities.product.*;
import com.task.exception.DataNotFoundException;
import com.task.repository.product.FeedPumpRepository;
import com.task.repository.product.PlateRepository;
import com.task.repository.product.PressRepository;
import com.task.repository.product.SqPumpRepository;
import com.task.service.outputService.InputService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final FeedPumpRepository feedPumpRepository;

    private static final double AIR_COMPRESS_DIVISOR = 28.28;
    private static final LocalTime SQ_OUTLET_DEFAULT_TIME = LocalTime.parse("00:20:00");



    @Override
    public DashboardResponse calculateDashboardData(InputRequest inputRequest) {
//        List<String> pressSizes = plateRepository.findDistinctPressSizeByPlateType(inputRequest.getPlateType());

//        List<String> pressSizes = plateRepository.findAll().stream()
//                .map(Plate::getPressSize)
//                .distinct()
//                .toList();

        List<String> pressSizes = inputRequest.getPressSizes();

        List<String> warnings = new ArrayList<>();

        String plateType = inputRequest.getPlateType();

        List<PressDataResponse> responseList = new ArrayList<>();
        List<PressTResponse> pressTResponses = new ArrayList<>();

        // SlurryResponse calculation
        double finalDensity = inputRequest.getDensityOfDrySolid() == null ? 1 : inputRequest.getDensityOfDrySolid();
        int totalDrySolid = (int) ((inputRequest.getSludgeQty() * 1000 * inputRequest.getDrySolidParticle() /100) / finalDensity);
        int totalWetCake= (int) (totalDrySolid * (100 / (100 - inputRequest.getMoistureContain())));

        SlurryResponse slurryResponse = new SlurryResponse();
        slurryResponse.setTotalDrySolid(totalDrySolid);
        slurryResponse.setTotalWetCake(totalWetCake);

        for (String pressSize : pressSizes) {
            boolean skip = false;

            Press press = pressRepository.findByPressSize(pressSize);
            if (press == null) {
                String msg = "Press not found for size " + pressSize;
                log.warn(msg);
                warnings.add(msg);
                skip = true;
            }

            Plate plate = plateRepository.findByPressSizeAndPlateType(pressSize, inputRequest.getPlateType());
            if (plate == null) {
                String msg = "Plate not found for size " + pressSize + " and type " + inputRequest.getPlateType();
                log.warn(msg);
                warnings.add(msg);
                skip = true;
            }

//            FeedPump feedPump = feedPumpRepository.findByPressSize(pressSize);
//            if (sqPump == null && inputRequest.getPlateType().equalsIgnoreCase("Membrane")) {
//                String msg = "SQ Pump not found for size " + pressSize;
//                log.warn(msg);
//                warnings.add(msg);
//                skip = true;
//            }

            SqPump sqPump = sqPumpRepository.findByPressSize(pressSize);
            if (sqPump == null && inputRequest.getPlateType().equalsIgnoreCase("Membrane")) {
                String msg = "SQ Pump not found for size " + pressSize;
                log.warn(msg);
                warnings.add(msg);
                skip = true;
            }

            if (skip) {
                continue;
            }

            // Press Data Response calculation
            int onePlateVolume = plate.getVolume();
            int noOfPress = (inputRequest.getNoOfPress() == 0) ? 1 : inputRequest.getNoOfPress();
            int noOfBatch = (inputRequest.getNoOfBatch() == 0) ? 1 : inputRequest.getNoOfBatch();

            double calcChamber = Math.ceil((double) (totalWetCake / (noOfPress * noOfBatch)) / onePlateVolume);
            int noOfChamber = roundUpToEven(calcChamber);
            log.info("noOfChamber : {}", noOfChamber);

            int totalVolume = noOfChamber * onePlateVolume;

            int feedPumpFlow;
            if (!(noOfChamber > press.getMaxChamber())) {
                feedPumpFlow = (inputRequest.getCusFeedRate() == null) ? getFlowRateByChamberCount(pressSize, noOfChamber) : inputRequest.getCusFeedRate();
            } else {
                feedPumpFlow = 1;
            }

            int airCompressDeli = (int) Math.ceil(totalVolume / AIR_COMPRESS_DIVISOR);

            Integer sqWaterUsed;
            Double sqFlowRate;
            Double selectedFlowRate = null;
            Integer sqTankCap;

            if ("Membrane".equalsIgnoreCase(plateType)) {
                assert sqPump != null;
                int sqInletWater = sqPump.getSqInletWater();
                sqWaterUsed = sqInletWater * (noOfChamber / 2);

                List<Double> flowRates = sqPump.getFlowRates().stream()
                        .map(SqCalcFR::getFlowRate)
                        .sorted()
                        .toList();

                for (Double rate : flowRates) {
                    if (((sqWaterUsed) / (rate * 1000 /60)) <= sqPump.getSqMaxTMin()) {
                        selectedFlowRate = rate;
                        break;
                    }
                }

                sqFlowRate = (noOfChamber > press.getMaxChamber()) ? selectedFlowRate : 0;
                log.info("sqFlowRate : {}", sqFlowRate);

                int calcSqTankCap = sqWaterUsed + (int) (sqWaterUsed * 0.3);
                sqTankCap = roundUpToNextHundred(calcSqTankCap);
            } else {
                sqWaterUsed = null;
                sqFlowRate = null;
                sqTankCap = null;
            }


            long pumpOnSeconds =
                    press.getCwFwdT().toSecondOfDay() +
                            press.getCwFwdDT().toSecondOfDay() +
                            press.getCwRevT().toSecondOfDay();

            double flowRateLitersPerSecond = ((press.getCwFlowRate() / 1.5) * 1000.0) / 3600.0;
            int cw1PWaterUsed = (int) (flowRateLitersPerSecond * pumpOnSeconds);
            int cw1CWaterUsed = cw1PWaterUsed * noOfChamber;
            int calcCwTankCap = cw1CWaterUsed + (int) (cw1CWaterUsed * 0.3);
            int cwTankCap = roundUpToNextHundred(calcCwTankCap);

            // Set PressDataResponse
            PressDataResponse pressData = new PressDataResponse();
            pressData.setPressSize(pressSize);
            pressData.setPlateType(inputRequest.getPlateType());
            pressData.setOnePlateVolume(onePlateVolume);
            pressData.setNoOfPress(noOfPress);
            pressData.setNoOfBatch(noOfBatch);
            pressData.setNoOfChamber(noOfChamber);

            pressData.setTotalVolume(totalVolume);
            pressData.setFeedPumpFlow(feedPumpFlow);
            pressData.setAirCompressDeli(airCompressDeli);

            pressData.setSqFlowRate(sqFlowRate);
            pressData.setSqWaterUsed(sqWaterUsed);
            pressData.setSqTankCap(sqTankCap);

            pressData.setCw1PWaterUsed(cw1PWaterUsed);
            pressData.setCw1CWaterUsed(cw1CWaterUsed);
            pressData.setCwTankCap(cwTankCap);

            if (noOfChamber > press.getMaxChamber()) {
                String msg = String.format(
                        "Calculated number of chambers (%d) exceeds the press's maximum capacity (%d).",
                        noOfChamber, press.getMaxChamber()
                );
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

            } else {
                pressData.setMessage(null);
            }


            if ("Membrane".equalsIgnoreCase(plateType)){
                pressData.setAirCompressDeli(null);
            } else {
                pressData.setSqFlowRate(null);
                pressData.setSqWaterUsed(null);
                pressData.setSqTankCap(null);
            }

            if (!inputRequest.isClothWashing()){
                pressData.setCw1PWaterUsed(null);
                pressData.setCw1CWaterUsed(null);
                pressData.setCwTankCap(null);
            }

            responseList.add(pressData);

            // Press Time Response calculation
            // Pressing Cycle Time (Duration)

            int dtCloseDT = (press.getDtAvailable()) ? press.getDtClosedT().toSecondOfDay() : 0;
            int dtOpenDT = (press.getDtAvailable()) ? press.getDtOpenT().toSecondOfDay() : 0;

            Duration pressingCTime = Duration.ofSeconds(
                    dtCloseDT + press.getCyFwdT().toSecondOfDay()
            );
            LocalTime pressingCT = LocalTime.MIDNIGHT.plus(pressingCTime);

            // Feed Time (Duration)
            long feedTimeSeconds =  (feedPumpFlow !=0) ? (int) ((inputRequest.getSludgeQty() * 3600) / (noOfPress * noOfBatch * feedPumpFlow)) : 0;
            Duration feedTime = Duration.ofSeconds(feedTimeSeconds);
            LocalTime feedT = LocalTime.MIDNIGHT.plus(feedTime);

            // Cake Washing Time (Duration)
            LocalTime cakeWT = (inputRequest.isCakeWashing()) ? inputRequest.getWashingT() : null;

            // Air Time (Duration)
            LocalTime cakeAirT = press.getCakeAirT();

            // Squeezing Time (Duration)
            Duration sqInletTime;
            LocalTime sqInletT;
            LocalTime sqOutletT;
            if ("Membrane".equalsIgnoreCase(plateType)) {
                long sqInletTimeSecond = (sqFlowRate != 0) ? (long) ((sqWaterUsed) / (sqFlowRate * 1000 / 3600)) : 0;
                sqInletTime = Duration.ofSeconds(sqInletTimeSecond);
                sqInletT = LocalTime.MIDNIGHT.plus(sqInletTime);

                sqOutletT = (inputRequest.getSqOutletT() == null)
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
                long onePlatePsTimeSecond = press.getPsFwdT().toSecondOfDay() +
                        press.getPsFwdDT().toSecondOfDay() +
                        press.getPsRevT().toSecondOfDay() +
                        press.getPsRevDT().toSecondOfDay();
                Duration onePlatePsTime = Duration.ofSeconds(onePlatePsTimeSecond);
                onePlatePsT = LocalTime.MIDNIGHT.plus(onePlatePsTime);

                long onCyclePsTimeSecond = ( press.getCyRevT().toSecondOfDay() +
                        dtOpenDT +
                        (long) onePlatePsT.toSecondOfDay() * noOfChamber) +
                        ((long) press.getPsFwdT().toSecondOfDay() * (noOfChamber + 2));
                Duration onCyclePsTime = Duration.ofSeconds(onCyclePsTimeSecond);
                onCyclePsT = LocalTime.MIDNIGHT.plus(onCyclePsTime);
            } else {
                onePlatePsT = null;
                onCyclePsT = null;
            }

            LocalTime onePlateCwT;
            LocalTime onCycleCwT;
            if (press.getCwAvailable()) {

                //ClothWashing Time (Duration)
                long onePlateCwTimeSecond = press.getPsFwdT().toSecondOfDay() +
                        press.getPsFwdDT().toSecondOfDay() +
                        (press.getPsRevT().toSecondOfDay() / 2) +
                        press.getPsRevDT().toSecondOfDay() +
                        press.getCwDownT().toSecondOfDay() +
                        press.getCwDownDT().toSecondOfDay() +
                        press.getCwFwdT().toSecondOfDay() +
                        press.getCwFwdDT().toSecondOfDay() +
                        press.getCwRevT().toSecondOfDay() +
                        press.getCwRevDT().toSecondOfDay() +
                        press.getCwUpT().toSecondOfDay() +
                        press.getCwUpDT().toSecondOfDay() +
                        (press.getPsRevT().toSecondOfDay() / 2) +
                        press.getPsRevDT().toSecondOfDay();
                Duration onePlateCwTime = Duration.ofSeconds(onePlateCwTimeSecond);
                onePlateCwT = LocalTime.MIDNIGHT.plus(onePlateCwTime);

                long onCycleCwTimeSecond = press.getCyFwdT().toSecondOfDay() +
                        press.getCyRevT().toSecondOfDay() +
                        dtCloseDT +
                        ((long) onePlateCwT.toSecondOfDay() * noOfChamber) +
                        ((long) press.getPsFwdT().toSecondOfDay() * (noOfChamber + 2));
                Duration onCycleCwTime = Duration.ofSeconds(onCycleCwTimeSecond);
                onCycleCwT = LocalTime.MIDNIGHT.plus(onCycleCwTime);
            } else {
                onePlateCwT = null;
                onCycleCwT = null;
            }

            PressTResponse pressTimeResponse = new PressTResponse();
            pressTimeResponse.setPressingCT(pressingCT);
            pressTimeResponse.setFeedT(feedT);
            pressTimeResponse.setCakeAirT(cakeAirT);
            pressTimeResponse.setSqInletT(sqInletT);
            pressTimeResponse.setSqOutletT(sqOutletT);
            pressTimeResponse.setOnePlatePsT(onePlatePsT);
            pressTimeResponse.setOnCyclePsT(onCyclePsT);
            pressTimeResponse.setOnePlateCwT(onePlateCwT);
            pressTimeResponse.setOnCycleCwT(onCycleCwT);
            pressTimeResponse.setCakeWT(cakeWT);

            if (noOfChamber > press.getMaxChamber()) {
                String msg = String.format(
                        "Calculated number of chambers (%d) exceeds the press's maximum capacity (%d).",
                        noOfChamber, press.getMaxChamber()
                );
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

            if (!inputRequest.isClothWashing()){
                pressTimeResponse.setOnePlateCwT(null);
                pressTimeResponse.setOnCycleCwT(null);
                pressTimeResponse.setCakeWT(null);
            }

            pressTResponses.add(pressTimeResponse);
        }

        // Build and return DashboardResponse
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setPressDataResponse(responseList);
        dashboardResponse.setPressTResponse(pressTResponses);
        dashboardResponse.setSlurryResponse(slurryResponse);
        dashboardResponse.setWarnings(warnings);

        return dashboardResponse;
    }

    // Feed Pump Selection Method
    public int getFlowRateByChamberCount(String pressSize, int noOfChamber) {
        FeedPump feedPump = feedPumpRepository.findByPressSize(pressSize)
                .orElseThrow(() -> new DataNotFoundException("FeedPump not found for press size: " + pressSize));

        for (ChamberRange range : feedPump.getChamberRanges()) {
            String[] parts = range.getRangeLabel().split("-");
            int lower = Integer.parseInt(parts[0].trim());
            int upper = Integer.parseInt(parts[1].trim());

            if (noOfChamber >= lower && noOfChamber <= upper) {
                return range.getFlowRate();
            }
        }

        throw new RuntimeException("No matching flow rate for chamber count: " + noOfChamber);
    }

    public static int roundUpToEven(double value) {
        int ceil = (int) Math.ceil(value);
        return (ceil % 2 == 0) ? ceil : ceil + 1;
    }

    public static int roundUpToNextHundred(int number) {
        return (number % 100 == 0) ? number : ((number + 99) / 100) * 100;
    }
}