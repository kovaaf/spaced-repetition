package org.company.spacedrepetitionbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private DefaultDeckConfig defaultDeck;

    @Data
    public static class DefaultDeckConfig {
        private String name;
        private RepoConfig repo;
        private SyncConfig sync;

        @Data
        public static class RepoConfig {
            private String url;
            private String branch;
            private String path;
            private String webhookSecret;
            private String token;
            private List<String> sourceFolders = Collections.emptyList();
            private List<String> excludeFolders = Collections.emptyList();
        }

        @Data
        public static class SyncConfig {
            private boolean initialEnabled;
            private String cron;
        }
    }
}
