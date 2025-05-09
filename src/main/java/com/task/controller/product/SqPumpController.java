package com.task.controller.product;

import com.task.dto.productRequest.SqPumpRequest;
import com.task.dto.response.SqPumpResponse;
import com.task.service.product.SqPumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/squeezing")
@RequiredArgsConstructor
public class SqPumpController {

    private final SqPumpService sqPumpService;


    @PostMapping("/addPump")
    public ResponseEntity<SqPumpResponse> addSqPump(@Valid @RequestBody SqPumpRequest sqPumpRequest) {
        return ResponseEntity.ok(sqPumpService.addSqPump(sqPumpRequest)) ;
    }
}