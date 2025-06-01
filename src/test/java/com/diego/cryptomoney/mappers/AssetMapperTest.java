package com.diego.cryptomoney.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.diego.cryptomoney.entities.AssetEntity;
import com.diego.cryptomoney.model.AssetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AssetMapperTest {

  private AssetMapper assetMapper;

  @BeforeEach
  void setUp() {
    assetMapper = Mappers.getMapper(AssetMapper.class);
  }

  @Nested
  @DisplayName("toAssetEntity")
  class ToAssetEntity {

    @Test
    @DisplayName("should map AssetDTO to AssetEntity correctly")
    void shouldMapAssetDTOToAssetEntityCorrectly() {
      AssetDTO assetDTO = new AssetDTO();
      assetDTO.setId(1L);
      assetDTO.setSymbol("BTC");

      AssetEntity assetEntity = assetMapper.toAssetEntity(assetDTO);

      assertNotNull(assetEntity);
      assertEquals(assetDTO.getId(), assetEntity.getId());
      assertEquals(assetDTO.getSymbol(), assetEntity.getSymbol());
    }

    @Test
    @DisplayName("should handle null AssetDTO")
    void shouldHandleNullAssetDTO() {
      AssetEntity assetEntity = assetMapper.toAssetEntity(null);

      assertNull(assetEntity);
    }
  }

  @Nested
  @DisplayName("toAssetDTO")
  class ToAssetDTO {

    @Test
    @DisplayName("should map AssetEntity to AssetDTO correctly")
    void shouldMapAssetEntityToAssetDTOCorrectly() {
      AssetEntity assetEntity = new AssetEntity();
      assetEntity.setId(1L);
      assetEntity.setSymbol("ETH");

      AssetDTO assetDTO = assetMapper.toAssetDTO(assetEntity);

      assertNotNull(assetDTO);
      assertEquals(assetEntity.getId(), assetDTO.getId());
      assertEquals(assetEntity.getSymbol(), assetDTO.getSymbol());
    }

    @Test
    @DisplayName("should handle null AssetEntity")
    void shouldHandleNullAssetEntity() {
      AssetDTO assetDTO = assetMapper.toAssetDTO(null);

      assertNull(assetDTO);
    }
  }
}
