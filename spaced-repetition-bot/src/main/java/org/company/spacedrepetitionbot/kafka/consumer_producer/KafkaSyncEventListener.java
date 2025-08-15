package org.company.spacedrepetitionbot.kafka.consumer_producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.company.spacedrepetitionbot.service.SyncEventProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSyncEventListener {

    private final SyncEventProcessor syncEventProcessor;
    private final RetryExecutor retryExecutor;

    @KafkaListener(topics = "sync-events", groupId = "sync-group")
    public void handleSyncEvent(SyncEventDTO event) {
        retryExecutor.executeWithRetry(() -> syncEventProcessor.processSyncEvent(event));
    }
}
