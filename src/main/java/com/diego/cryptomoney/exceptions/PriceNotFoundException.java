package com.diego.cryptomoney.exceptions;

public class PriceNotFoundException extends RuntimeException {
  public PriceNotFoundException(String symbol) {
    super("Price for symbol " + symbol + " not found");
  }
}
