package com.diego.cryptomoney.advices;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SimpleProblem {
  int status;
  String message;
}
