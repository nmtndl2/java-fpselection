package com.task.service.outputService.impl;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.outputResponse.DashboardResponse;
import com.task.dto.outputResponse.PressDataResponse;
import com.task.dto.outputResponse.PressTResponse;
import com.task.dto.outputResponse.SlurryResponse;
import com.task.entities.product.ChamberRange;
import com.task.entities.product.FeedPump;
import com.task.entities.product.Plate;
import com.task.entities.product.Press;
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
        List<String> pressSizes = pressRepository.findAllPressSizes();
        List<PressDataResponse> responseList = new ArrayList<>();
        List<PressTResponse> pressTResponses = new ArrayList<>();

        double totalDrySolid = inputRequest.getDensityOfDrySolid() * (inputRequest.getDrySolidParticle());
        double totalWetCake= totalDrySolid * (100 / (100 - inputRequest.getMoistureContain()));

        // Example SlurryResponse calculation
        SlurryResponse slurryResponse = new SlurryResponse();
        slurryResponse.setTotalDrySolid(totalDrySolid);
        slurryResponse.setTotalWetCake(totalWetCake);

        for (String pressSize : pressSizes) {
            Press press = pressRepository.findByPressSize(pressSize);

            Plate plate = plateRepository.findByPressSizeAndPlateType(pressSize, inputRequest.getPlateType());
            int onePlateVolume = plate.getVolume();

            int noOfPress = (inputRequest.getNoOfPress() == 0) ? 1 : inputRequest.getNoOfPress();
            int noOfBatch = (inputRequest.getNoOfBatch() == 0) ? 1 : inputRequest.getNoOfBatch();

            int noOfChamber = (int) Math.ceil((totalWetCake / (noOfPress * noOfBatch)) / onePlateVolume);
            int validNoOfChamber = (noOfChamber<=press.getMaxChamber()) ? noOfChamber : 0;

            int totalVolume = validNoOfChamber * onePlateVolume;

            int feedPumpFlow = getFlowRateByChamberCount(pressSize, validNoOfChamber);
            double airCompressDeli = (totalVolume / 28.28);
            int sqInletWater = sqPumpRepository.findByPressSize(pressSize).getSqInletWater();
            int sqWaterUsed = sqInletWater * (validNoOfChamber / 2);
            int sqTankCap = sqWaterUsed + (int) (sqWaterUsed * 0.3);

            long pumpOnSeconds =
                    press.getCwFwdT().toSecondOfDay() +
                            press.getCwFwdDT().toSecondOfDay() +
                            press.getCwRevT().toSecondOfDay();

            double flowRateLitersPerSecond = (press.getCwFlowRate() * 1000.0) / 3600.0;
            int cw1PWaterUsed = (int) (flowRateLitersPerSecond * pumpOnSeconds);
            int cw1CWaterUsed = cw1PWaterUsed * noOfChamber;
            int cwTankCap = cw1CWaterUsed + (int) (cw1CWaterUsed * 0.3);

            // Set PressDataResponse
            PressDataResponse pressData = new PressDataResponse();
            pressData.setPressSize(pressSize);
            pressData.setPlateType(inputRequest.getPlateType());
            pressData.setOnePlateVolume(onePlateVolume);
            pressData.setNoOfPress(noOfPress);
            pressData.setNoOfBatch(noOfBatch);
            pressData.setNoOfChamber(validNoOfChamber);
            if (noOfChamber > press.getMaxChamber()) {
                pressData.setMessage("Calculated chambers: " + noOfChamber + ", Max allowed: "+ press.getMaxChamber() +" (Exceeds limit)");
            } else {
                pressData.setMessage("Calculated chambers: " + noOfChamber + ", Max allowed: "+ press.getMaxChamber());
            }
            pressData.setTotalVolume(totalVolume);
            pressData.setFeedPumpFlow(feedPumpFlow);
            pressData.setAirCompressDeli(airCompressDeli);
            pressData.setSqWaterUsed(sqWaterUsed);
            pressData.setSqTankCap(sqTankCap);
            pressData.setCw1PWaterUsed(cw1PWaterUsed);
            pressData.setCw1CWaterUsed(cw1CWaterUsed);
            pressData.setCwTankCap(cwTankCap);

            responseList.add(pressData);

            // Pressing Cycle Time (Duration)
            Duration pressingCTime = Duration.ofSeconds(
                    press.getDtClosedT().toSecondOfDay() +
                            press.getCyFwdT().toSecondOfDay()
            );
            LocalTime pressingCT = LocalTime.MIDNIGHT.plus(pressingCTime);

            // Feed Time (Duration)
            long feedTimeSeconds = (int) ((inputRequest.getSludgeQty() * 3600) / (noOfPress * noOfBatch * feedPumpFlow));
            Duration feedTime = Duration.ofSeconds(feedTimeSeconds);
            LocalTime feedT = LocalTime.MIDNIGHT.plus(feedTime);

            // Air Time (Duration)
            LocalTime cakeWT = null;

            // Air Time (Duration)
            LocalTime cakeAirT = press.getCakeAirT();

            // Squeezing Time (Duration)
            long sqInletTimeSecond = ((sqWaterUsed * 3600) / (feedPumpFlow));
            Duration sqInletTime = Duration.ofSeconds(sqInletTimeSecond);
            LocalTime sqInletT = LocalTime.MIDNIGHT.plus(sqInletTime);

            //#################################################################
            LocalTime sqOutletT = null;

            // Plate Shifter Time (Duration)
            long onePlatePSTimeSecond = press.getPsFwdT().toSecondOfDay() +
                                            press.getPsFwdDT().toSecondOfDay() +
                                                press.getPsRevT().toSecondOfDay() +
                                                    press.getPsRevDT().toSecondOfDay();
            Duration onePlatePSTime = Duration.ofSeconds(onePlatePSTimeSecond);
            LocalTime onePlatePST = LocalTime.MIDNIGHT.plus(onePlatePSTime);

            long onCyclePSTimeSecond = (onePlatePST.getSecond() * noOfChamber); //##################
            LocalTime onCyclePST = null;

            // ClothWashing Time (Duration)
            LocalTime onePlateCwT = null;
            LocalTime onCycleCwT = null;

            PressTResponse pressT = new PressTResponse();
            pressT.setPressingCT(pressingCT);
            pressT.setFeedT(feedT);
            pressT.setCakeAirT(cakeAirT);
            pressT.setSqInletT(sqInletT);
            pressT.setSqOutletT(sqOutletT);
            pressT.setOnePlatePST(onePlatePST);
            pressT.setOnCyclePST(onCyclePST);
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
}
