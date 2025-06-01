package com.diego.cryptomoney.exceptions;

import java.util.function.Supplier;

public class WalletNotFoundException extends RuntimeException {
  public WalletNotFoundException(Long walletId) {
    super("Wallet with id " + walletId + " not found");
  }

  public static Supplier<WalletNotFoundException> with(Long walletId) {
    return () -> new WalletNotFoundException(walletId);
  }
}
