package com.diego.cryptomoney.coincap;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class CoinDataHistory {
    private BigDecimal priceUsd;
    private Long time;
    private OffsetDateTime date;
}
