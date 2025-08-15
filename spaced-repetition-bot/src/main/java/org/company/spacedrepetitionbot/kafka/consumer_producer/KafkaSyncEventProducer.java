package org.company.spacedrepetitionbot.kafka.consumer_producer;

import lombok.RequiredArgsConstructor;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSyncEventProducer {
    private final KafkaTemplate<String, SyncEventDTO> kafkaTemplate;
    @Value("${spring.kafka.topic.sync.events}") private String syncTopic;

    public void sendSyncEvent(SyncEventDTO event) {
        String key = event.getDeckId() != null ?
                event.getDeckId()
                        .toString() :
                "global-sync";
        kafkaTemplate.send(syncTopic, key, event);
    }
}
