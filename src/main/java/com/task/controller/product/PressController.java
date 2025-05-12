package com.task.controller.product;

import com.task.dto.productRequest.PressRequest;
import com.task.dto.response.PressResponse;
import com.task.service.product.PressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/press")
@RequiredArgsConstructor
public class PressController {

    private final PressService pressService;

    @PostMapping("/addPress")
    public ResponseEntity<PressResponse> addPress(@Valid @RequestBody PressRequest pressRequest) {
        return ResponseEntity.ok(pressService.addPress(pressRequest));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PressResponse>> getAll() {
        return ResponseEntity.ok(pressService.getAll());
    }

}
