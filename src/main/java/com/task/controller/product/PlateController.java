package com.task.controller.product;

import com.task.dto.productRequest.PlateRequest;
import com.task.dto.response.PlateResponse;
import com.task.service.product.PlateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plate")
@RequiredArgsConstructor
public class PlateController {

    private final PlateService plateService;

    @PostMapping("/addplate")
    public ResponseEntity<PlateResponse> addPlate(@Valid @RequestBody PlateRequest plateRequest){
        return ResponseEntity.ok(plateService.addPlate(plateRequest));
    }

    @PutMapping("/updateplate/{plateId}")
    public ResponseEntity<PlateResponse> updatePlate(@PathVariable Long plateId, @RequestBody PlateRequest plateRequest) {
        return ResponseEntity.ok(plateService.updatePlate(plateId, plateRequest));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<PlateResponse >> getAll(){
        return ResponseEntity.ok(plateService.getAll());
    }

    @GetMapping("/get/{plateId}")
    public ResponseEntity<PlateResponse> getById(@PathVariable Long plateId){
        return ResponseEntity.ok(plateService.getById(plateId));
    }

    @DeleteMapping("/delete/{plateId}")
    public ResponseEntity<String> deleteById(@PathVariable Long plateId){
        return ResponseEntity.ok(plateService.deleteById(plateId));
    }
}
