package com.diego.cryptomoney.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cryptomoney")
@Data
public class CryptoMoneyConfig {
  private int priceUpdateInterval;

  private int maxThreads;
  private String coinCapBaseUrl;
  private String apiKey;
}
