package com.task.controller.product;

import com.task.dto.productRequest.PlateRequest;
import com.task.dto.response.PlateResponse;
import com.task.service.product.PlateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plate")
@RequiredArgsConstructor
public class PlateController {

    private final PlateService plateService;

    @PostMapping("/addplate")
    public ResponseEntity<PlateResponse> addPlate(@Valid @RequestBody PlateRequest plateRequest){
        return ResponseEntity.ok(plateService.addPlate(plateRequest));
    }
}
