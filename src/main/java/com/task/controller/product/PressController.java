package com.task.controller.product;

import com.task.dto.productRequest.PressRequest;
import com.task.dto.response.PressResponse;
import com.task.service.product.PressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/press")
@RequiredArgsConstructor
public class PressController {

    private final PressService pressService;

    @PostMapping("/addPress")
    public ResponseEntity<PressResponse> addPress(@Valid @RequestBody PressRequest pressRequest) {
        return ResponseEntity.ok(pressService.addPress(pressRequest));
    }
}
