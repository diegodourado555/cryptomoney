package com.diego.cryptomoney.services;

import com.diego.cryptomoney.model.AssetDTO;
import com.diego.cryptomoney.model.ProfitSimulationRequestDTO;
import com.diego.cryptomoney.model.ProfitSimulationResultDTO;

public interface AssetService {

  void updateAssetPrices(String symbol);

  AssetDTO addAssetToWallet(Long walletId, AssetDTO assetDTO);

  ProfitSimulationResultDTO simulateProfit(ProfitSimulationRequestDTO profitSimulationRequestDTO);
}
