package com.npdev.estore.product_service.command.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/management")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventReplayController {

    private final EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventProcessor/{processorName}/reset")
    // processorName is product-group (application.properties)
    public ResponseEntity<String> replayEvents(@PathVariable("processorName") String processorName) {
        log.info("Replaying events...");
        Optional<TrackingEventProcessor> optionalTrackingEventProcessor = eventProcessingConfiguration.eventProcessor(
                processorName, TrackingEventProcessor.class
        );

        if (optionalTrackingEventProcessor.isPresent()) {
            TrackingEventProcessor eventProcessor = optionalTrackingEventProcessor.get();
            eventProcessor.shutDown();
            eventProcessor.resetTokens();
            eventProcessor.start();
            return ResponseEntity.ok()
                    .body(String.format("The event processor with a name [%s] has been reset", eventProcessor.getName()));
        } else {
            return ResponseEntity.badRequest().body("Only Tracking event processor is supported.");
        }
    }

}
