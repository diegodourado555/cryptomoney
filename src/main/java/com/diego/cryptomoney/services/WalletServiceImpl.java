package com.diego.cryptomoney.services;

import com.diego.cryptomoney.entities.UserEntity;
import com.diego.cryptomoney.entities.WalletEntity;
import com.diego.cryptomoney.exceptions.UserAlreadyExistsException;
import com.diego.cryptomoney.exceptions.WalletNotFoundException;
import com.diego.cryptomoney.mappers.WalletMapper;
import com.diego.cryptomoney.model.WalletDTO;
import com.diego.cryptomoney.repositories.UserRepository;
import com.diego.cryptomoney.repositories.WalletRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {
  private final WalletRepository walletRepository;
  private final UserRepository userRepository;
  private final WalletMapper walletMapper;

  public WalletDTO createWallet(WalletDTO walletDTO) {
    log.info("Creating wallet with request: {}", walletDTO);
    WalletEntity walletEntity = walletMapper.toWalletEntity(walletDTO);
    userRepository
        .findByEmail(walletDTO.getEmail())
        .ifPresentOrElse(
            user -> {
              log.error("User with email {} not found", walletDTO.getEmail());
              throw new UserAlreadyExistsException(walletDTO.getEmail());
            },
            () -> {
              String username =
                  walletDTO.getEmail() != null
                      ? walletDTO.getEmail().substring(0, walletDTO.getEmail().indexOf("@"))
                      : "defaultUserName";
              UserEntity createdUser =
                  userRepository.saveAndFlush(
                      UserEntity.builder().email(walletDTO.getEmail()).username(username).build());
              walletEntity.setUser(createdUser);
              walletEntity.setExternalId(UUID.randomUUID().toString());
              walletEntity.setTotal(BigDecimal.ZERO);
            });

    return walletMapper.toWalletDTO(walletRepository.save(walletEntity));
  }

  @Override
  public WalletDTO getWalletById(Long walletId) {
    return walletMapper.toWalletDTO(
        walletRepository.findById(walletId).orElseThrow(WalletNotFoundException.with(walletId)));
  }
}
