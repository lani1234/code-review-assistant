package io.github.lani1234.codereview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "review")
public class ReviewConfig {
    private List<String> fileExtensions;
    private int maxFileSize;
    private List<String> aspects;
}