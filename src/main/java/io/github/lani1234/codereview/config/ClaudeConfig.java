package io.github.lani1234.codereview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "claude.api")
public class ClaudeConfig {
    private String key;
    private String url;
    private String model;
    private int maxTokens;
    private String version;
}