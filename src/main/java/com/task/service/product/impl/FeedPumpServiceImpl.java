package com.task.service.product.impl;

import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import com.task.entities.product.FeedPump;
import com.task.exception.AlreadyExistsException;
import com.task.exception.BadRequestException;
import com.task.mapper.FeedPumpMapper;
import com.task.repository.product.FeedPumpRepository;
import com.task.repository.product.PressRepository;
import com.task.service.product.FeedPumpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedPumpServiceImpl implements FeedPumpService {

    private final FeedPumpRepository feedPumpRepository;
    private final FeedPumpMapper feedPumpMapper;
    private final PressRepository pressRepository;

    @Override
    public FeedPumpResponse createFeedPump(FeedPumpRequest feedPumpRequest) {

        // 1. Check if pressSize exists in PressRepository
        boolean pressExists = pressRepository.existsByPressSize(feedPumpRequest.getPressSize());
        if (!pressExists) {
            throw new BadRequestException("Invalid press size. Not found in press repository.");
        }

        // 2. Check if feed pump already exists for pressSize
        boolean isExist = feedPumpRepository.existsByPressSize(feedPumpRequest.getPressSize());
        if (isExist) {
            throw new AlreadyExistsException("FeedPump with this press size already exists.");
        }

        // 3. Validate chamberRanges
        validateChamberRanges(feedPumpRequest);

        // 4. Mapping and saving
        FeedPump feedPump = feedPumpMapper.reqToEntity(feedPumpRequest);
        feedPump.getChamberRanges().forEach(chamberRange -> chamberRange.setFeedPump(feedPump));
        FeedPump savedFeedPump = feedPumpRepository.save(feedPump);
        return feedPumpMapper.entityToResp(savedFeedPump);
    }

    @Override
    public void deleteFeedPumpById(Long id) {
        boolean exists = feedPumpRepository.existsById(id);
        if (!exists) {
            throw new BadRequestException("FeedPump with press size " + id + " does not exist.");
        }
        feedPumpRepository.deleteById(id);
    }

    private void validateChamberRanges(FeedPumpRequest feedPumpRequest) {
        List<String> rangeLabels = feedPumpRequest.getChamberRanges().stream()
                .map(c -> c.getRangeLabel())
                .sorted(Comparator.comparingInt(r -> Integer.parseInt(r.split("-")[0])))
                .toList();

        // Validate format and continuity
        int expectedStart = 0;
        for (String range : rangeLabels) {
            String[] parts = range.split("-");
            if (parts.length != 2) {
                throw new BadRequestException("Invalid range format: " + range);
            }
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);

            if (start != expectedStart) {
                throw new BadRequestException("Chamber ranges are not continuous or start at wrong point. Expected start: " + expectedStart);
            }
            expectedStart = end + 1;
        }

        // Validate against max chamber size from press repository
        int maxRangeEnd = Integer.parseInt(rangeLabels.get(rangeLabels.size() - 1).split("-")[1]);
        int maxChamber = pressRepository.findMaxChamberByPressSize(feedPumpRequest.getPressSize())
                .orElseThrow(() -> new BadRequestException("Max chamber not defined for press size: " + feedPumpRequest.getPressSize()));

        if (maxRangeEnd > maxChamber) {
            throw new BadRequestException("Last chamber range exceeds max chamber size: " + maxChamber);
        }
    }
}
