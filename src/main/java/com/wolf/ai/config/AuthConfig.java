package com.wolf.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
	private String apiId;
	private String apiKey;
	private String secKey;
}
