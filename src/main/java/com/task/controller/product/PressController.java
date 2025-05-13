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

    @GetMapping("/get/{pressId}")
    public ResponseEntity<PressResponse> getPress(@PathVariable Long pressId) {
        return ResponseEntity.ok(pressService.getPress(pressId));
    }

    @PutMapping("/updatePress/{pressId}")
    public ResponseEntity<PressResponse> updatePress(@PathVariable Long pressId, @RequestBody PressRequest pressRequest) {
        return ResponseEntity.ok(pressService.updatePress(pressId, pressRequest));
    }

    @DeleteMapping("/{pressId}")
    public ResponseEntity<String> deletePress(@PathVariable Long pressId) {
        pressService.deletePressById(pressId);
        return ResponseEntity.ok("Press with press size " + pressId + " deleted successfully.");
    }

}