package org.company.spacedrepetitionbot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.dto.WebhookPayload;
import org.company.spacedrepetitionbot.service.GitHubWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// для работы нужен статический адрес\домен - устанавливается в вебхуке на GitHub. Можно получить временный с помощью
// ngrok (для его работы нужен впн)
@Slf4j
@RestController
@RequestMapping("/webhook/github")
@RequiredArgsConstructor
public class GitHubWebhookController {
    private final GitHubWebhookService gitHubWebhookService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature", required = false) String signature,
            @RequestBody WebhookPayload payload) {

        log.debug("Received GitHub webhook with event: {}", event);
        gitHubWebhookService.processWebhook(event, payload, signature);
        return ResponseEntity.ok("Webhook processed successfully");
    }
}