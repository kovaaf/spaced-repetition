package org.company.spacedrepetitionbot.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.company.spacedrepetitionbot.config.AppProperties;
import org.company.spacedrepetitionbot.dto.WebhookPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebhookValidator {
    private final AppProperties appProperties;

    public void validateSignature(WebhookPayload payload, String signature) {
        String secret = appProperties.getDefaultDeck()
                .getRepo()
                .getWebhookSecret();
        String computedSignature = "sha1=" +
                new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secret).hmacHex(payload.toString());

        if (!computedSignature.equals(signature)) {
            throw new SecurityException("Invalid webhook signature");
        }
    }
}
