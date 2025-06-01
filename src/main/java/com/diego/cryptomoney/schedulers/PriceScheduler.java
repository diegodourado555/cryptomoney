package com.diego.cryptomoney.schedulers;

import com.diego.cryptomoney.config.CryptoMoneyConfig;
import com.diego.cryptomoney.repositories.AssetRepository;
import com.diego.cryptomoney.services.AssetService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceScheduler {
  private final AssetService assetService;
  private final CryptoMoneyConfig cryptoMoneyConfig;
  private final AssetRepository assetRepository;

  private ExecutorService executorService;

  @PostConstruct
  public void init() {
    executorService = Executors.newFixedThreadPool(cryptoMoneyConfig.getMaxThreads());
  }

  @PreDestroy
  public void cleanup() {
    executorService.shutdown();
  }

  @Scheduled(fixedRateString = "${cryptomoney.price-update-interval}")
  public void updateAssetPrices() {
    log.info("*** Starting price update for all assets");
    List<String> symbols = assetRepository.findAllSymbols();
    for (int i = 0; i < symbols.size(); i += cryptoMoneyConfig.getMaxThreads()) {
      List<String> batch =
          symbols.stream().skip(i).limit(cryptoMoneyConfig.getMaxThreads()).toList();
      List<CompletableFuture<Void>> futures =
          batch.stream()
              .map(
                  symbol ->
                      CompletableFuture.runAsync(
                          () -> assetService.updateAssetPrices(symbol), executorService))
              .toList();
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
    log.info("** Price update for all assets completed");
  }
}
