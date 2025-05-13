package com.task.controller.product;

import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import com.task.repository.product.FeedPumpRepository;
import com.task.service.product.FeedPumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed-pumps")
@RequiredArgsConstructor
public class FeedPumpController {

    private final FeedPumpRepository feedPumpRepository;
    private final FeedPumpService feedPumpService;

    @PostMapping
    public ResponseEntity<FeedPumpResponse> createFeedPump(@Valid @RequestBody FeedPumpRequest feedPumpRequest) {
        return ResponseEntity.ok(feedPumpService.createFeedPump(feedPumpRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedPump(@PathVariable Long id) {
        feedPumpService.deleteFeedPumpById(id);
        return ResponseEntity.ok("FeedPump with press size " + id + " deleted successfully.");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<FeedPumpResponse> updateFeedPump(@PathVariable Long id, @RequestBody FeedPumpRequest feedPumpRequest) {
        return ResponseEntity.ok(feedPumpService.updateFeedPump(id, feedPumpRequest));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<FeedPumpResponse> getPump(@PathVariable Long id) {
        return ResponseEntity.ok(feedPumpService.getPump(id));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<FeedPumpResponse>> getAllPump() {
        return ResponseEntity.ok(feedPumpService.getAllPump());
    }


}
