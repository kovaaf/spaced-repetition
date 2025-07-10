package org.company.spacedrepetitionbot.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncEventDTO {
    private Long deckId;
    private boolean forceFullSync;
    private List<String> changedFiles;
}