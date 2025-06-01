package com.diego.cryptomoney.services;

import com.diego.cryptomoney.coincap.CoinCapHistoryResponse;
import com.diego.cryptomoney.coincap.CoinCapResponse;
import com.diego.cryptomoney.coincap.CoinData;
import com.diego.cryptomoney.coincap.CoinDataHistory;
import com.diego.cryptomoney.config.CryptoMoneyConfig;
import com.diego.cryptomoney.entities.AssetEntity;
import com.diego.cryptomoney.entities.WalletEntity;
import com.diego.cryptomoney.exceptions.PriceNotFoundException;
import com.diego.cryptomoney.exceptions.WalletNotFoundException;
import com.diego.cryptomoney.mappers.AssetMapper;
import com.diego.cryptomoney.model.AssetDTO;
import com.diego.cryptomoney.model.ProfitSimulationRequestDTO;
import com.diego.cryptomoney.model.ProfitSimulationResultDTO;
import com.diego.cryptomoney.repositories.AssetRepository;
import com.diego.cryptomoney.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {
  private final AssetRepository assetRepository;
  private final AssetMapper assetMapper;
  private final CryptoMoneyConfig cryptoMoneyConfig;
  private final RestTemplate restTemplate;
  private final WalletRepository walletRepository;

  public AssetDTO createAsset(AssetDTO assetDTO) {
    log.info("Creating asset with request: {}", assetDTO);
    return assetMapper.toAssetDTO(assetRepository.save(assetMapper.toAssetEntity(assetDTO)));
  }

  @Override
  public void updateAssetPrices(String symbol) {
    try {
      CoinData matchingCoin = getCoinCapData(symbol);
      if (matchingCoin != null && matchingCoin.getPriceUsd() != null) {
        assetRepository.updateAssetPrice(matchingCoin.getPriceUsd(), symbol);
        log.info("-> Updated price for asset {}: {}", symbol, matchingCoin.getPriceUsd());
      } else {
        log.warn("! No matching coin found for symbol: {}", symbol);
      }
    } catch (Exception e) {
      log.error("! Error updating price for asset {}. ERROR: {}", symbol, e.getMessage());
    }
  }

  @Override
  public AssetDTO addAssetToWallet(Long walletId, AssetDTO assetDTO) {
    if (assetDTO.getSymbol() != null) {
      CoinData matchingCoin = getCoinCapData(assetDTO.getSymbol());
      if (matchingCoin == null || matchingCoin.getPriceUsd() == null) {
        throw new PriceNotFoundException(assetDTO.getSymbol());
      }
    }
    AssetEntity assetEntity = assetMapper.toAssetEntity(assetDTO);
    WalletEntity walletEntity =
        walletRepository.findById(walletId).orElseThrow(WalletNotFoundException.with(walletId));
    walletEntity.setTotal(calculateWalletTotal(walletEntity, assetEntity));
    assetEntity.setWallet(walletEntity);
    return assetMapper.toAssetDTO(assetRepository.save(assetEntity));
  }

  @Override
  public ProfitSimulationResultDTO simulateProfit(
      ProfitSimulationRequestDTO profitSimulationRequestDTO) {
    String bestAsset = null;
    String worstAssert = null;
    double bestPerformance = -Double.MAX_VALUE;
    double worstPerformance = Double.MAX_VALUE;
    BigDecimal totalValue = BigDecimal.ZERO;

    if (profitSimulationRequestDTO == null
        || CollectionUtils.isEmpty(profitSimulationRequestDTO.getAssets())) {
      log.warn("No assets provided for profit simulation.");
      return ProfitSimulationResultDTO.builder()
          .total(BigDecimal.ZERO)
          .bestAsset(bestAsset)
          .bestPerformance(BigDecimal.ZERO)
          .worstAsset(worstAssert)
          .worstPerformance(BigDecimal.ZERO)
          .build();
    }

    for (AssetDTO asset : profitSimulationRequestDTO.getAssets()) {
      if (asset.getSymbol() != null) {
        List<CoinDataHistory> coinPriceHistoryList = getCoinCapDataHistory(asset.getSymbol());
        if (coinPriceHistoryList == null || coinPriceHistoryList.isEmpty()) {
          log.warn("No price history found for asset: {}", asset.getSymbol());
          continue;
        }

        BigDecimal initialPrice = coinPriceHistoryList.getFirst().getPriceUsd();
        BigDecimal finalPrice = coinPriceHistoryList.getLast().getPriceUsd();

        BigDecimal quantity =
            asset.getQuantity() == null ? BigDecimal.ZERO : BigDecimal.valueOf(asset.getQuantity());
        BigDecimal assetValue = finalPrice.multiply(quantity);
        totalValue = totalValue.add(assetValue);

        double percentageChange = 0;
        if (initialPrice.compareTo(BigDecimal.ZERO) > 0) {
          percentageChange =
              finalPrice
                  .subtract(initialPrice)
                  .divide(initialPrice, 4, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100))
                  .doubleValue();
        }

        if (percentageChange > bestPerformance) {
          bestPerformance = percentageChange;
          bestAsset = asset.getSymbol();
        }

        if (percentageChange < worstPerformance) {
          worstPerformance = percentageChange;
          worstAssert = asset.getSymbol();
        }

      } else {
        log.warn("Asset symbol is null, skipping price update for asset: {}", asset);
      }
    }
    return ProfitSimulationResultDTO.builder()
        .total(BigDecimal.valueOf(totalValue.doubleValue()))
        .bestAsset(bestAsset)
        .bestPerformance(BigDecimal.valueOf(bestPerformance))
        .worstAsset(worstAssert)
        .worstPerformance(BigDecimal.valueOf(worstPerformance))
        .build();
  }

  private BigDecimal calculateWalletTotal(WalletEntity walletEntity, AssetEntity assetEntity) {
    BigDecimal currentTotal =
        walletEntity.getTotal() != null ? walletEntity.getTotal() : BigDecimal.ZERO;
    BigDecimal assetValue = assetEntity.getPrice().multiply(assetEntity.getQuantity());
    return currentTotal.add(assetValue.multiply(assetEntity.getQuantity()));
  }

  private CoinData getCoinCapData(String symbol) {
    String url = cryptoMoneyConfig.getCoinCapBaseUrl() + "/assets?search=" + symbol.toLowerCase();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + cryptoMoneyConfig.getApiKey());
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<CoinCapResponse> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, CoinCapResponse.class);
    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      CoinData matchingCoin =
          response.getBody().getData().stream()
              .filter(coin -> coin.getSymbol().equalsIgnoreCase(symbol))
              .findFirst()
              .orElse(null);

      if (matchingCoin != null && matchingCoin.getPriceUsd() != null) {
        return matchingCoin;
      } else {
        log.warn("No matching coin found for symbol: {}", symbol);
        return null;
      }
    }
    return null;
  }

  private List<CoinDataHistory> getCoinCapDataHistory(String symbol) {
    CoinData matchingCoin = getCoinCapData(symbol);
    if (matchingCoin != null) {
      String url =
          cryptoMoneyConfig.getCoinCapBaseUrl()
              + "/assets/"
              + matchingCoin.getId()
              + "/history?interval=m15";
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + cryptoMoneyConfig.getApiKey());
      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<CoinCapHistoryResponse> response =
          restTemplate.exchange(url, HttpMethod.GET, entity, CoinCapHistoryResponse.class);
      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return response.getBody().getData().stream()
            .sorted(Comparator.comparing(CoinDataHistory::getDate))
            .toList();
      } else {
        log.warn("No matching coin found for symbol: {}", symbol);
        return null;
      }
    }
    return null;
  }
}
