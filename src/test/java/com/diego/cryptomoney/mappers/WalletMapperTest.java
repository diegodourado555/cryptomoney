package com.diego.cryptomoney.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.diego.cryptomoney.entities.UserEntity;
import com.diego.cryptomoney.entities.WalletEntity;
import com.diego.cryptomoney.model.WalletDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

class WalletMapperTest {

  private WalletMapper walletMapper;

  @BeforeEach
  void setUp() {
    walletMapper = Mappers.getMapper(WalletMapper.class);
  }

  @Test
  @DisplayName("should map WalletDTO to WalletEntity correctly")
  void shouldMapWalletDTOToWalletEntityCorrectly() {
    WalletDTO walletDTO =
        WalletDTO.builder()
            .id(1L)
            .email("test@example.com")
            .total(BigDecimal.TEN)
            .build();

    WalletEntity walletEntity = walletMapper.toWalletEntity(walletDTO);

    assertNotNull(walletEntity);
    assertEquals(walletDTO.getId(), walletEntity.getId());
    assertEquals(walletDTO.getTotal(), walletEntity.getTotal());
  }

  @Test
  @DisplayName("should handle null WalletDTO")
  void shouldHandleNullWalletDTO() {
    WalletEntity walletEntity = walletMapper.toWalletEntity(null);
    assertNull(walletEntity);
  }

  @Test
  @DisplayName("should map WalletEntity to WalletDTO correctly")
  void shouldMapWalletEntityToWalletDTOCorrectly() {
    WalletEntity walletEntity = new WalletEntity();
    walletEntity.setId(2L);
    walletEntity.setTotal(BigDecimal.ONE);

    WalletDTO walletDTO = walletMapper.toWalletDTO(walletEntity);

    assertNotNull(walletDTO);
    assertEquals(walletEntity.getId(), walletDTO.getId());
    assertEquals(walletEntity.getTotal(), walletDTO.getTotal());
  }

  @Test
  @DisplayName("should handle null WalletEntity")
  void shouldHandleNullWalletEntity() {
    WalletDTO walletDTO = walletMapper.toWalletDTO(null);
    assertNull(walletDTO);
  }

  @Test
  @DisplayName("should map user fields in @AfterMapping")
  void shouldMapUserFieldsInAfterMapping() {
    UserEntity user =
        UserEntity.builder().id(10L).email("user@domain.com").username("user").build();

    WalletEntity walletEntity = new WalletEntity();
    walletEntity.setId(3L);
    walletEntity.setUser(user);

    WalletDTO walletDTO = walletMapper.toWalletDTO(walletEntity);

    assertNotNull(walletDTO);
    assertEquals(user.getEmail(), walletDTO.getEmail());
    assertEquals(user.getId(), walletDTO.getUserId());
  }
}
