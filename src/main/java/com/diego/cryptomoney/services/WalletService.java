package com.diego.cryptomoney.services;

import com.diego.cryptomoney.model.WalletDTO;

public interface WalletService {

  WalletDTO createWallet(WalletDTO walletDTO);

  WalletDTO getWalletById(Long walletId);
}
