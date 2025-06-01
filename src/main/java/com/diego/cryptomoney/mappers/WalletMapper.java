package com.diego.cryptomoney.mappers;

import com.diego.cryptomoney.entities.WalletEntity;
import com.diego.cryptomoney.model.WalletDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface WalletMapper {
  WalletEntity toWalletEntity(WalletDTO walletDTO);

  WalletDTO toWalletDTO(WalletEntity walletEntity);

  @AfterMapping
  default void mapUserFields(
      WalletEntity walletEntity, @MappingTarget WalletDTO.WalletDTOBuilder walletDTO) {
    if (walletEntity.getUser() != null) {
      walletDTO.email(walletEntity.getUser().getEmail());
      walletDTO.userId(walletEntity.getUser().getId());
    }
  }
}
