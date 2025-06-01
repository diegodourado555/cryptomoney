package com.diego.cryptomoney.coincap;

import java.util.List;
import lombok.Data;

@Data
public class CoinCapHistoryResponse {
  private List<CoinDataHistory> data;
  private long timestamp;
}
