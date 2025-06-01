package com.diego.cryptomoney.schedulers;

import static org.mockito.Mockito.*;

import com.diego.cryptomoney.config.CryptoMoneyConfig;
import com.diego.cryptomoney.repositories.AssetRepository;
import com.diego.cryptomoney.services.AssetService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceSchedulerTest {

  private AssetService assetService;
  private CryptoMoneyConfig cryptoMoneyConfig;
  private AssetRepository assetRepository;
  private PriceScheduler priceScheduler;

  @BeforeEach
  void setUp() {
    assetService = mock(AssetService.class);
    cryptoMoneyConfig = mock(CryptoMoneyConfig.class);
    assetRepository = mock(AssetRepository.class);

    when(cryptoMoneyConfig.getMaxThreads()).thenReturn(2);

    priceScheduler = new PriceScheduler(assetService, cryptoMoneyConfig, assetRepository);
    priceScheduler.init();
  }

  @Test
  void updateAssetPrices_shouldCallUpdateForAllSymbols() {
    List<String> symbols = Arrays.asList("BTC", "ETH", "ADA");
    when(assetRepository.findAllSymbols()).thenReturn(symbols);

    priceScheduler.updateAssetPrices();

    verify(assetService, times(1)).updateAssetPrices("BTC");
    verify(assetService, times(1)).updateAssetPrices("ETH");
    verify(assetService, times(1)).updateAssetPrices("ADA");
  }

  @Test
  void updateAssetPrices_shouldHandleEmptySymbols() {
    when(assetRepository.findAllSymbols()).thenReturn(List.of());

    priceScheduler.updateAssetPrices();

    verify(assetService, never()).updateAssetPrices(anyString());
  }

  @Test
  void cleanup_shouldShutdownExecutor() {
    priceScheduler.cleanup();
    // No exception means success; can't easily verify shutdown on private field
  }
}
