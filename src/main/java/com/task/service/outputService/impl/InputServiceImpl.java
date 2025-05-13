package com.task.service.outputService.impl;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.outputResponse.DashboardResponse;
import com.task.dto.outputResponse.PressDataResponse;
import com.task.dto.outputResponse.PressTResponse;
import com.task.dto.outputResponse.SlurryResponse;
import com.task.entities.product.*;
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

    @Override
    public DashboardResponse calculateDashboardData(InputRequest inputRequest) {
        List<String> pressSizes = plateRepository.findDistinctPressSizeByPlateType(inputRequest.getPlateType());
        List<String> warnings = new ArrayList<>();

        List<PressDataResponse> responseList = new ArrayList<>();
        List<PressTResponse> pressTResponses = new ArrayList<>();

        // SlurryResponse calculation
        double finalDensity = inputRequest.getDensityOfDrySolid() == 0 ? 1 : inputRequest.getDensityOfDrySolid();
        double totalDrySolid = (inputRequest.getSludgeQty() * inputRequest.getDrySolidParticle()) / finalDensity;
        double totalWetCake= totalDrySolid * (100 / (100 - inputRequest.getMoistureContain()));

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

            SqPump sqPump = sqPumpRepository.findByPressSize(pressSize);
            if (sqPump == null) {
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

            Double calcChamber = Math.ceil((totalWetCake / (noOfPress * noOfBatch)) / onePlateVolume);
            int noOfChamber = roundUpToEven(calcChamber);
//            int validNoOfChamber = (noOfChamber<=press.getMaxChamber()) ? noOfChamber : 0;

            int totalVolume = noOfChamber * onePlateVolume;

            Integer feedPumpFlow;
            if (!(noOfChamber > press.getMaxChamber())) {
                feedPumpFlow = getFlowRateByChamberCount(pressSize, noOfChamber);
            } else {
                feedPumpFlow = 1;
            }

            int airCompressDeli = (int) Math.ceil(totalVolume / 28.28);

            int sqInletWater = sqPumpRepository.findByPressSize(pressSize).getSqInletWater();
            int sqWaterUsed = sqInletWater * (noOfChamber / 2);

            List<Double> flowRates = sqPump.getFlowRates().stream()
                    .map(SqCalcFR::getFlowRate)
                    .sorted()
                    .toList();

            Double selectedFlowRate = null;
            for (Double rate : flowRates) {
                if (((sqWaterUsed) / (rate * 1000 /60)) <= sqPump.getSqMaxTMin()) {
                    selectedFlowRate = rate;
                    break;
                }
            }

            Double sqFlowRate = selectedFlowRate;
            log.info("sqFlowRate : {}", sqFlowRate);

            int calcSqTankCap = sqWaterUsed + (int) (sqWaterUsed * 0.3);
            int sqTankCap = roundUpToNextHundred(calcSqTankCap);


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

            if (!"Membrane".equalsIgnoreCase(inputRequest.getPlateType())) {
                pressData.setSqWaterUsed(null);
                pressData.setSqTankCap(null);
            }

            responseList.add(pressData);

            // Press Time Response calculation
            // Pressing Cycle Time (Duration)
            Duration pressingCTime = Duration.ofSeconds(
                    press.getDtClosedT().toSecondOfDay() +
                            press.getCyFwdT().toSecondOfDay()
            );
            LocalTime pressingCT = LocalTime.MIDNIGHT.plus(pressingCTime);

            // Feed Time (Duration)
            long feedTimeSeconds =  (feedPumpFlow !=0) ? (int) ((inputRequest.getSludgeQty() * 3600) / (noOfPress * noOfBatch * feedPumpFlow)) : 0;
            Duration feedTime = Duration.ofSeconds(feedTimeSeconds);
            LocalTime feedT = LocalTime.MIDNIGHT.plus(feedTime);

            // Air Time (Duration)
            LocalTime cakeWT = inputRequest.getWashingT();

            // Air Time (Duration)
            LocalTime cakeAirT = press.getCakeAirT();

            // Squeezing Time (Duration)
            long sqInletTimeSecond = (sqFlowRate != 0) ? (long) ((sqWaterUsed) / (sqFlowRate * 1000 / 3600)) : 0;
            Duration sqInletTime = Duration.ofSeconds(sqInletTimeSecond);
            LocalTime sqInletT = LocalTime.MIDNIGHT.plus(sqInletTime);

            LocalTime sqOutletT = (inputRequest.getSqOutletT() == null)
                    ? LocalTime.parse("00:20:00")
                    : inputRequest.getSqOutletT();

            // Plate Shifter Time (Duration)
            long onePlatePsTimeSecond = press.getPsFwdT().toSecondOfDay() +
                    press.getPsFwdDT().toSecondOfDay() +
                    press.getPsRevT().toSecondOfDay() +
                    press.getPsRevDT().toSecondOfDay();
            Duration onePlatePsTime = Duration.ofSeconds(onePlatePsTimeSecond);
            LocalTime onePlatePsT = LocalTime.MIDNIGHT.plus(onePlatePsTime);

            long onCyclePsTimeSecond = ((long) onePlatePsT.toSecondOfDay() * noOfChamber) +
                    ((long) press.getPsFwdT().toSecondOfDay() * (noOfChamber + 2));
            Duration onCyclePsTime = Duration.ofSeconds(onCyclePsTimeSecond);
            LocalTime onCyclePsT = LocalTime.MIDNIGHT.plus(onCyclePsTime);

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
            LocalTime onePlateCwT = LocalTime.MIDNIGHT.plus(onePlateCwTime);

            long onCycleCwTimeSecond = ((long) onePlateCwT.toSecondOfDay() * noOfChamber) +
                    ((long) press.getPsFwdT().toSecondOfDay() * (noOfChamber + 2));
            Duration onCycleCwTime = Duration.ofSeconds(onCycleCwTimeSecond);
            LocalTime onCycleCwT = LocalTime.MIDNIGHT.plus(onCycleCwTime);

            PressTResponse pressT = new PressTResponse();
            pressT.setPressingCT(pressingCT);
            pressT.setFeedT(feedT);
            pressT.setCakeAirT(cakeAirT);
            pressT.setSqInletT(sqInletT);
            pressT.setSqOutletT(sqOutletT);
            pressT.setOnePlatePsT(onePlatePsT);
            pressT.setOnCyclePsT(onCyclePsT);
            pressT.setOnePlateCwT(onePlateCwT);
            pressT.setOnCycleCwT(onCycleCwT);
            pressT.setCakeWT(cakeWT);

            pressTResponses.add(pressT);
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
                .orElseThrow(() -> new RuntimeException("FeedPump not found for press size: " + pressSize));

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
