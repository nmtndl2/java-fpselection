package com.task.controller.product;

import com.task.dto.productRequest.FeedPumpRequest;
import com.task.dto.response.FeedPumpResponse;
import com.task.repository.product.FeedPumpRepository;
import com.task.service.product.FeedPumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @GetMapping
//    public ResponseEntity<List<FeedPump>> getAllFeedPumps() {
//        return ResponseEntity.ok(feedPumpService.getAllFeedPump());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<FeedPump> getFeedPumpById(@PathVariable Long id) {
//        Optional<FeedPump> feedPump = feedPumpRepository.findById(id);
//        return ResponseEntity.ok(feedPumpService.getFeedPumpById(id));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<FeedPump> updateFeedPump(@PathVariable Long id, @RequestBody FeedPump updatedFeedPump) {
//        return ResponseEntity.ok(feedPumpService.updateFeedPump(id, updatedFeedPump));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFeedPump(@PathVariable Long id) {
//        try {
//            feedPumpService.deleteFeedPump(id);
//            return ResponseEntity.noContent().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

}
