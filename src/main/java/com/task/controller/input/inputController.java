package com.task.controller.input;

import com.task.dto.inputRequest.InputRequest;
import com.task.dto.outputResponse.DashboardResponse;
import com.task.dto.outputResponse.PressDataResponse;
import com.task.service.outputService.InputService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/input")
@RequiredArgsConstructor
public class inputController {

    private final InputService inputService;

    @PostMapping
    public ResponseEntity<DashboardResponse> calcPressSize(@Valid @RequestBody InputRequest inputRequest) {
        DashboardResponse response = inputService.calculateDashboardData(inputRequest);
        return ResponseEntity.ok(response);
    }
}
