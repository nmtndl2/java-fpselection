package com.task.controller.product;

import com.task.dto.productRequest.PlateTypeRequest;
import com.task.dto.response.PlateTypeResponse;
import com.task.service.product.PlateTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platetype")
@RequiredArgsConstructor
public class PlateTypeController {

    private final PlateTypeService plateTypeService;

    @PostMapping("/addplatetype")
    public ResponseEntity<PlateTypeResponse> addPlateType(@Valid @RequestBody PlateTypeRequest plateTypeRequest) {
        return ResponseEntity.ok(plateTypeService.addPlateType(plateTypeRequest));
    }
}
