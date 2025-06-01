package com.diego.cryptomoney.repositories;

import com.diego.cryptomoney.entities.AssetEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, Long> {
  @Modifying
  @Query(
      value =
          """
            UPDATE asset
            SET price = :price, value = :price * quantity
            WHERE symbol = :symbol
          """,
      nativeQuery = true)
  void updateAssetPrice(BigDecimal price, String symbol);

  @Query(
      value =
          """
            SELECT DISTINCT a.symbol
            FROM AssetEntity a
        """)
  List<String> findAllSymbols();
}
