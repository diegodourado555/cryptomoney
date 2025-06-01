package com.diego.cryptomoney.mappers;

import com.diego.cryptomoney.entities.AssetEntity;
import com.diego.cryptomoney.model.AssetDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AssetMapper {
  AssetEntity toAssetEntity(AssetDTO assetDTO);

  AssetDTO toAssetDTO(AssetEntity assetEntity);
}
