package com.task.controller.product;

import com.task.dto.productRequest.SqPumpRequest;
import com.task.dto.response.SqPumpResponse;
import com.task.entities.product.SqPump;
import com.task.service.product.SqPumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/squeezing")
@RequiredArgsConstructor
public class SqPumpController {

    private final SqPumpService sqPumpService;

    @PostMapping("/addPump")
    public ResponseEntity<SqPumpResponse> addSqPump(@Valid @RequestBody SqPumpRequest sqPumpRequest) {
        return ResponseEntity.ok(sqPumpService.addSqPump(sqPumpRequest)) ;
    }

    @GetMapping("/getall")
    public ResponseEntity<List<SqPump>> getAll() {
        return ResponseEntity.ok(sqPumpService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SqPumpResponse> getSqPump(@PathVariable Long id) {
        return ResponseEntity.ok(sqPumpService.getSqPump(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSqPump(@PathVariable Long id) {
        return ResponseEntity.ok(sqPumpService.deleteSqPump(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SqPumpResponse> updateSqPump(@PathVariable Long id, @RequestBody SqPumpRequest sqPumpRequest) {
        return ResponseEntity.ok(sqPumpService.updateSqPump(id, sqPumpRequest));
    }
}