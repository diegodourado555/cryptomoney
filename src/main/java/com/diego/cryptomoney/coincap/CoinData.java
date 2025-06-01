package com.diego.cryptomoney.coincap;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoinData {
    private String id;
    private String symbol;
    private BigDecimal priceUsd;
}
