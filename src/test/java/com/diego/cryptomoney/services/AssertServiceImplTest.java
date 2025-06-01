package com.diego.cryptomoney.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.diego.cryptomoney.coincap.CoinCapHistoryResponse;
import com.diego.cryptomoney.coincap.CoinCapResponse;
import com.diego.cryptomoney.coincap.CoinData;
import com.diego.cryptomoney.coincap.CoinDataHistory;
import com.diego.cryptomoney.config.CryptoMoneyConfig;
import com.diego.cryptomoney.entities.AssetEntity;
import com.diego.cryptomoney.entities.WalletEntity;
import com.diego.cryptomoney.mappers.AssetMapper;
import com.diego.cryptomoney.model.AssetDTO;
import com.diego.cryptomoney.model.ProfitSimulationRequestDTO;
import com.diego.cryptomoney.model.ProfitSimulationResultDTO;
import com.diego.cryptomoney.repositories.AssetRepository;
import com.diego.cryptomoney.repositories.WalletRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class AssertServiceImplTest {

  @Mock private AssetRepository assetRepository;
  @Mock private AssetMapper assetMapper;
  @Mock private CryptoMoneyConfig cryptoMoneyConfig;
  @Mock private RestTemplate restTemplate;
  @Mock private WalletRepository walletRepository;

  private AssetService assetService;

  @BeforeEach
  void setUp() {
    assetService =
        new AssetServiceImpl(
            assetRepository, assetMapper, cryptoMoneyConfig, restTemplate, walletRepository);
  }

  @Test
  @DisplayName("should calculate profit simulation with null asset quantity")
  void shouldCalculateProfitSimulationWithNullAssetQuantity() {
    ProfitSimulationRequestDTO requestDTO = new ProfitSimulationRequestDTO();
    AssetDTO asset = new AssetDTO();
    asset.setSymbol("BTC");
    asset.setQuantity(null);
    requestDTO.setAssets(List.of(asset));

    CoinData btcCoin = new CoinData();
    btcCoin.setSymbol("BTC");
    btcCoin.setId("bitcoin");
    btcCoin.setPriceUsd(BigDecimal.valueOf(60000));
    CoinCapResponse btcCoinCapResponse = new CoinCapResponse();
    btcCoinCapResponse.setData(List.of(btcCoin));
    ResponseEntity<CoinCapResponse> btcCoinResponse = ResponseEntity.ok(btcCoinCapResponse);

    CoinDataHistory btcHistory1 = new CoinDataHistory();
    btcHistory1.setPriceUsd(BigDecimal.valueOf(30000));
    btcHistory1.setDate(OffsetDateTime.now());
    CoinDataHistory btcHistory2 = new CoinDataHistory();
    btcHistory2.setPriceUsd(BigDecimal.valueOf(60000));
    btcHistory2.setDate(OffsetDateTime.now().minusDays(1));
    CoinCapHistoryResponse btcHistoryResponse = new CoinCapHistoryResponse();
    btcHistoryResponse.setData(List.of(btcHistory1, btcHistory2));
    ResponseEntity<CoinCapHistoryResponse> btcHistoryEntity = ResponseEntity.ok(btcHistoryResponse);

    when(cryptoMoneyConfig.getCoinCapBaseUrl()).thenReturn("http://mockurl");
    when(cryptoMoneyConfig.getApiKey()).thenReturn("mock-api-key");
    when(restTemplate.exchange(
            contains("/assets?search="),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(btcCoinResponse);

    when(restTemplate.exchange(
            contains("/history?interval=m15"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapHistoryResponse.class)))
        .thenReturn(btcHistoryEntity);

    ProfitSimulationResultDTO result = assetService.simulateProfit(requestDTO);

    assertNotNull(result);
    assertEquals("BTC", result.getBestAsset());
    assertEquals("BTC", result.getWorstAsset());
    assertEquals(BigDecimal.valueOf(-50.0), result.getBestPerformance());
    assertEquals(BigDecimal.valueOf(-50.0), result.getWorstPerformance());
    assertEquals(0, result.getTotal().compareTo(BigDecimal.ZERO));
  }

  @Test
  @DisplayName("should handle null asset symbol in profit simulation")
  void shouldHandleNullAssetSymbolInProfitSimulation() {
    ProfitSimulationRequestDTO requestDTO = new ProfitSimulationRequestDTO();
    AssetDTO asset = new AssetDTO();
    asset.setSymbol(null);
    asset.setQuantity(1.0);
    requestDTO.setAssets(List.of(asset));

    ProfitSimulationResultDTO result = assetService.simulateProfit(requestDTO);

    assertNotNull(result);
    assertNull(result.getBestAsset());
    assertNull(result.getWorstAsset());
    assertEquals(0, result.getTotal().compareTo(BigDecimal.ZERO));
  }

  @Test
  @DisplayName("should handle null assets list in profit simulation")
  void shouldHandleNullAssetsListInProfitSimulation() {
    ProfitSimulationRequestDTO requestDTO = new ProfitSimulationRequestDTO();
    requestDTO.setAssets(null);

    ProfitSimulationResultDTO result = assetService.simulateProfit(requestDTO);

    assertNotNull(result);
    assertNull(result.getBestAsset());
    assertNull(result.getWorstAsset());
    assertEquals(BigDecimal.ZERO, result.getTotal());
  }

  @Test
  @DisplayName("should handle empty price history for all assets")
  void shouldHandleEmptyPriceHistoryForAllAssets() {
    ProfitSimulationRequestDTO requestDTO = new ProfitSimulationRequestDTO();
    AssetDTO asset1 = new AssetDTO();
    asset1.setSymbol("BTC");
    asset1.setQuantity(1.0);
    AssetDTO asset2 = new AssetDTO();
    asset2.setSymbol("ETH");
    asset2.setQuantity(2.0);
    requestDTO.setAssets(List.of(asset1, asset2));

    CoinData btcCoin = new CoinData();
    btcCoin.setSymbol("BTC");
    btcCoin.setId("bitcoin");
    CoinCapResponse btcCoinCapResponse = new CoinCapResponse();
    btcCoinCapResponse.setData(List.of(btcCoin));
    ResponseEntity<CoinCapResponse> btcCoinResponse = ResponseEntity.ok(btcCoinCapResponse);

    CoinCapHistoryResponse btcHistoryResponse = new CoinCapHistoryResponse();
    btcHistoryResponse.setData(List.of());
    ResponseEntity<CoinCapHistoryResponse> btcHistoryEntity = ResponseEntity.ok(btcHistoryResponse);

    CoinData ethCoin = new CoinData();
    ethCoin.setSymbol("ETH");
    ethCoin.setId("ethereum");
    CoinCapResponse ethCoinCapResponse = new CoinCapResponse();
    ethCoinCapResponse.setData(List.of(ethCoin));
    ResponseEntity<CoinCapResponse> ethCoinResponse = ResponseEntity.ok(ethCoinCapResponse);

    CoinCapHistoryResponse ethHistoryResponse = new CoinCapHistoryResponse();
    ethHistoryResponse.setData(List.of());
    ResponseEntity<CoinCapHistoryResponse> ethHistoryEntity = ResponseEntity.ok(ethHistoryResponse);

    when(restTemplate.exchange(
            contains("/assets?search=btc"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(btcCoinResponse);

    when(restTemplate.exchange(
            contains("/assets?search=eth"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(ethCoinResponse);

    ProfitSimulationResultDTO result = assetService.simulateProfit(requestDTO);

    assertNotNull(result);
    assertNull(result.getBestAsset());
    assertNull(result.getWorstAsset());
    assertEquals(0, result.getTotal().compareTo(BigDecimal.ZERO));
  }

  @Test
  @DisplayName("should add asset to wallet successfully")
  void shouldAddAssetToWalletSuccessfully() {
    Long walletId = 1L;
    AssetDTO assetDTO = new AssetDTO();
    assetDTO.setSymbol("BTC");
    assetDTO.setQuantity(2.0);

    CoinData btcCoin = new CoinData();
    btcCoin.setSymbol("BTC");
    btcCoin.setId("bitcoin");
    btcCoin.setPriceUsd(BigDecimal.valueOf(50000));
    CoinCapResponse btcCoinCapResponse = new CoinCapResponse();
    btcCoinCapResponse.setData(List.of(btcCoin));
    ResponseEntity<CoinCapResponse> btcCoinResponse = ResponseEntity.ok(btcCoinCapResponse);

    AssetEntity assetEntity = new AssetEntity();
    assetEntity.setSymbol("BTC");
    assetEntity.setQuantity(BigDecimal.valueOf(2.0));
    assetEntity.setPrice(BigDecimal.valueOf(50000));

    WalletEntity walletEntity = new WalletEntity();
    walletEntity.setId(walletId);
    walletEntity.setTotal(BigDecimal.valueOf(1000));

    when(assetMapper.toAssetEntity(assetDTO)).thenReturn(assetEntity);
    when(walletRepository.findById(walletId)).thenReturn(java.util.Optional.of(walletEntity));
    when(assetRepository.save(any())).thenReturn(assetEntity);
    when(assetMapper.toAssetDTO(assetEntity)).thenReturn(assetDTO);
    when(cryptoMoneyConfig.getCoinCapBaseUrl()).thenReturn("http://mockurl");
    when(cryptoMoneyConfig.getApiKey()).thenReturn("mock-api-key");
    when(restTemplate.exchange(
            contains("/assets?search="),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(btcCoinResponse);

    AssetDTO result = assetService.addAssetToWallet(walletId, assetDTO);

    assertNotNull(result);
    assertEquals("BTC", result.getSymbol());
  }

  @Test
  @DisplayName("should throw PriceNotFoundException when coin data is missing")
  void shouldThrowPriceNotFoundExceptionWhenCoinDataMissing() {
    Long walletId = 1L;
    AssetDTO assetDTO = new AssetDTO();
    assetDTO.setSymbol("BTC");
    assetDTO.setQuantity(2.0);

    CoinCapResponse btcCoinCapResponse = new CoinCapResponse();
    btcCoinCapResponse.setData(List.of());
    ResponseEntity<CoinCapResponse> btcCoinResponse = ResponseEntity.ok(btcCoinCapResponse);

    when(cryptoMoneyConfig.getCoinCapBaseUrl()).thenReturn("http://mockurl");
    when(cryptoMoneyConfig.getApiKey()).thenReturn("mock-api-key");
    when(restTemplate.exchange(
            contains("/assets?search="),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(btcCoinResponse);

    assertThrows(
        com.diego.cryptomoney.exceptions.PriceNotFoundException.class,
        () -> assetService.addAssetToWallet(walletId, assetDTO));
  }

  @Test
  @DisplayName("should update asset prices successfully")
  void shouldUpdateAssetPricesSuccessfully() {
    String symbol = "BTC";
    CoinData btcCoin = new CoinData();
    btcCoin.setSymbol("BTC");
    btcCoin.setId("bitcoin");
    btcCoin.setPriceUsd(BigDecimal.valueOf(50000));
    CoinCapResponse btcCoinCapResponse = new CoinCapResponse();
    btcCoinCapResponse.setData(List.of(btcCoin));
    ResponseEntity<CoinCapResponse> btcCoinResponse = ResponseEntity.ok(btcCoinCapResponse);

    when(cryptoMoneyConfig.getCoinCapBaseUrl()).thenReturn("http://mockurl");
    when(cryptoMoneyConfig.getApiKey()).thenReturn("mock-api-key");
    when(restTemplate.exchange(
            contains("/assets?search="),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(btcCoinResponse);

    assetService.updateAssetPrices(symbol);

    org.mockito.Mockito.verify(assetRepository).updateAssetPrice(BigDecimal.valueOf(50000), symbol);
  }

  @Test
  @DisplayName("should not update asset prices if no matching coin")
  void shouldNotUpdateAssetPricesIfNoMatchingCoin() {
    String symbol = "BTC";
    CoinCapResponse btcCoinCapResponse = new CoinCapResponse();
    btcCoinCapResponse.setData(List.of());
    ResponseEntity<CoinCapResponse> btcCoinResponse = ResponseEntity.ok(btcCoinCapResponse);

    when(cryptoMoneyConfig.getCoinCapBaseUrl()).thenReturn("http://mockurl");
    when(cryptoMoneyConfig.getApiKey()).thenReturn("mock-api-key");
    when(restTemplate.exchange(
            contains("/assets?search="),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(btcCoinResponse);

    assetService.updateAssetPrices(symbol);

    org.mockito.Mockito.verify(assetRepository, org.mockito.Mockito.never())
        .updateAssetPrice(any(), any());
  }

  @Test
  @DisplayName("should handle exception in updateAssetPrices gracefully")
  void shouldHandleExceptionInUpdateAssetPricesGracefully() {
    String symbol = "BTC";
    when(cryptoMoneyConfig.getCoinCapBaseUrl()).thenThrow(new RuntimeException("fail"));

    assetService.updateAssetPrices(symbol);
  }

  @Test
  @DisplayName("should skip asset with zero quantity in simulateProfit")
  void shouldSkipAssetWithZeroQuantityInSimulateProfit() {
    ProfitSimulationRequestDTO requestDTO = new ProfitSimulationRequestDTO();
    AssetDTO asset = new AssetDTO();
    asset.setSymbol("BTC");
    asset.setQuantity(0.0);
    requestDTO.setAssets(List.of(asset));

    CoinData btcCoin = new CoinData();
    btcCoin.setSymbol("BTC");
    btcCoin.setId("bitcoin");
    btcCoin.setPriceUsd(BigDecimal.valueOf(60000));
    CoinCapResponse btcCoinCapResponse = new CoinCapResponse();
    btcCoinCapResponse.setData(List.of(btcCoin));
    ResponseEntity<CoinCapResponse> btcCoinResponse = ResponseEntity.ok(btcCoinCapResponse);

    CoinDataHistory btcHistory1 = new CoinDataHistory();
    btcHistory1.setPriceUsd(BigDecimal.valueOf(30000));
    btcHistory1.setDate(OffsetDateTime.now());
    CoinDataHistory btcHistory2 = new CoinDataHistory();
    btcHistory2.setPriceUsd(BigDecimal.valueOf(60000));
    btcHistory2.setDate(OffsetDateTime.now().minusDays(1));
    CoinCapHistoryResponse btcHistoryResponse = new CoinCapHistoryResponse();
    btcHistoryResponse.setData(List.of(btcHistory1, btcHistory2));
    ResponseEntity<CoinCapHistoryResponse> btcHistoryEntity = ResponseEntity.ok(btcHistoryResponse);

    when(cryptoMoneyConfig.getCoinCapBaseUrl()).thenReturn("http://mockurl");
    when(cryptoMoneyConfig.getApiKey()).thenReturn("mock-api-key");
    when(restTemplate.exchange(
            contains("/assets?search="),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapResponse.class)))
        .thenReturn(btcCoinResponse);

    when(restTemplate.exchange(
            contains("/history?interval=m15"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CoinCapHistoryResponse.class)))
        .thenReturn(btcHistoryEntity);

    ProfitSimulationResultDTO result = assetService.simulateProfit(requestDTO);

    assertNotNull(result);
    assertEquals("BTC", result.getBestAsset());
    assertEquals("BTC", result.getWorstAsset());
    assertEquals(BigDecimal.valueOf(-50.0), result.getBestPerformance());
    assertEquals(BigDecimal.valueOf(-50.0), result.getWorstPerformance());
    assertEquals(0, result.getTotal().compareTo(BigDecimal.ZERO));
  }
}
