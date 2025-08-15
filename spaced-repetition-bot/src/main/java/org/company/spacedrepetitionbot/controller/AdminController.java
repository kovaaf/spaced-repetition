package org.company.spacedrepetitionbot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.kafka.consumer_producer.KafkaSyncEventProducer;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.company.spacedrepetitionbot.service.DeckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final KafkaSyncEventProducer syncEventProducer;
    private final DeckService deckService;

    @GetMapping("/force-sync")
    public ResponseEntity<String> forceSync() {
        syncEventProducer.sendSyncEvent(new SyncEventDTO(
                deckService.getDefaultDeck()
                        .getDeckId(), true, null));
        return ResponseEntity.ok("Sync queued successfully");
    }
}
