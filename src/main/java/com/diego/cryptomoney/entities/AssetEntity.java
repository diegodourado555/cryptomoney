package com.diego.cryptomoney.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asset")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private WalletEntity wallet;

  @Column(name = "symbol", nullable = false, length = 20)
  private String symbol;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal quantity;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal price;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal value;
}
