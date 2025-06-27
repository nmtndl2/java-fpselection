package com.task.controller.input;

import com.task.dto.input.request.InputRequest;
import com.task.dto.output.response.DashboardResponse;
import com.task.service.output.service.InputService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/input")
@RequiredArgsConstructor
public class InputController {

    private final InputService inputService;

    @PostMapping
    public ResponseEntity<DashboardResponse> calcPressSize(@Valid @RequestBody InputRequest inputRequest) {
        DashboardResponse response = inputService.calculateDashboardData(inputRequest);
        return ResponseEntity.ok(response);
    }
}
