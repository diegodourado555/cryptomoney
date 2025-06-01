package com.diego.cryptomoney.controllers;

import com.diego.cryptomoney.api.WalletApi;
import com.diego.cryptomoney.model.AssetDTO;
import com.diego.cryptomoney.model.ProfitSimulationRequestDTO;
import com.diego.cryptomoney.model.ProfitSimulationResultDTO;
import com.diego.cryptomoney.model.WalletDTO;
import com.diego.cryptomoney.services.AssetService;
import com.diego.cryptomoney.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class CryptoMoneyController implements WalletApi {

  private final WalletService walletService;
  private final AssetService assetService;

  @Override
  public ResponseEntity<WalletDTO> createWallet(WalletDTO walletDTO) {
    return ResponseEntity.ok(walletService.createWallet(walletDTO));
  }

  @Override
  public ResponseEntity<AssetDTO> addAssetToWallet(Long walletId, AssetDTO assetDTO) {
    return ResponseEntity.ok(assetService.addAssetToWallet(walletId, assetDTO));
  }

  @Override
  public ResponseEntity<WalletDTO> getWalletById(Long walletId) {
    return ResponseEntity.ok(walletService.getWalletById(walletId));
  }

  @Override
  public ResponseEntity<ProfitSimulationResultDTO> simulateProfit(
      ProfitSimulationRequestDTO profitSimulationRequestDTO) {
    return ResponseEntity.ok(assetService.simulateProfit(profitSimulationRequestDTO));
  }
}
