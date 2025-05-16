package com.task.controller.product;

import com.task.dto.productRequest.PlateTypeRequest;
import com.task.dto.response.PlateTypeResponse;
import com.task.entities.product.PlateType;
import com.task.repository.product.PlateTypeRepository;
import com.task.service.product.PlateTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platetype")
@RequiredArgsConstructor
public class PlateTypeController {

    private final PlateTypeService plateTypeService;
    private final PlateTypeRepository plateTypeRepository;

    @PostMapping("/addplatetype")
    public ResponseEntity<PlateTypeResponse> addPlateType(@Valid @RequestBody PlateTypeRequest plateTypeRequest) {
        return ResponseEntity.ok(plateTypeService.addPlateType(plateTypeRequest));
    }

    @DeleteMapping("/delete/{plateTypeId}")
    public ResponseEntity<String> deletePlateType(@PathVariable Long plateTypeId) {
        return ResponseEntity.ok(plateTypeService.deletePlateType(plateTypeId));
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<PlateType>> getAllPlateTypes() {
        return ResponseEntity.ok(plateTypeService.findAll());
    }
}
