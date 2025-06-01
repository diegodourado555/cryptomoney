package com.diego.cryptomoney.coincap;

import java.util.List;
import lombok.Data;

@Data
public class CoinCapResponse {
  private List<CoinData> data;
  private long timestamp;
}
