package com.diego.cryptomoney.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.diego.cryptomoney.entities.UserEntity;
import com.diego.cryptomoney.entities.WalletEntity;
import com.diego.cryptomoney.exceptions.UserAlreadyExistsException;
import com.diego.cryptomoney.exceptions.WalletNotFoundException;
import com.diego.cryptomoney.mappers.WalletMapper;
import com.diego.cryptomoney.model.WalletDTO;
import com.diego.cryptomoney.repositories.UserRepository;
import com.diego.cryptomoney.repositories.WalletRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

  @Mock private WalletRepository walletRepository;
  @Mock private UserRepository userRepository;
  @Mock private WalletMapper walletMapper;

  private WalletServiceImpl walletService;

  @BeforeEach
  void setUp() {
    walletService = new WalletServiceImpl(walletRepository, userRepository, walletMapper);
  }

  @Test
  @DisplayName("should create wallet and user when user does not exist")
  void shouldCreateWalletAndUserWhenUserDoesNotExist() {
    WalletDTO walletDTO = new WalletDTO();
    walletDTO.setEmail("test@example.com");

    WalletEntity walletEntity = new WalletEntity();
    UserEntity userEntity = UserEntity.builder().email("test@example.com").username("test").build();
    WalletEntity savedWalletEntity = new WalletEntity();
    WalletDTO resultWalletDTO = new WalletDTO();

    when(walletMapper.toWalletEntity(walletDTO)).thenReturn(walletEntity);
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(userEntity);
    when(walletRepository.save(any(WalletEntity.class))).thenReturn(savedWalletEntity);
    when(walletMapper.toWalletDTO(savedWalletEntity)).thenReturn(resultWalletDTO);

    WalletDTO result = walletService.createWallet(walletDTO);

    assertNotNull(result);
    verify(userRepository).saveAndFlush(any(UserEntity.class));
    verify(walletRepository).save(any(WalletEntity.class));
    verify(walletMapper).toWalletDTO(savedWalletEntity);
  }

  @Test
  @DisplayName("should throw UserAlreadyExistsException when user already exists")
  void shouldThrowUserAlreadyExistsExceptionWhenUserAlreadyExists() {
    WalletDTO walletDTO = new WalletDTO();
    walletDTO.setEmail("test@example.com");

    UserEntity userEntity = UserEntity.builder().email("test@example.com").username("test").build();

    when(walletMapper.toWalletEntity(walletDTO)).thenReturn(new WalletEntity());
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

    assertThrows(UserAlreadyExistsException.class, () -> walletService.createWallet(walletDTO));
    verify(userRepository, never()).saveAndFlush(any());
    verify(walletRepository, never()).save(any());
  }

  @Test
  @DisplayName("should create wallet with default username when email is null")
  void shouldCreateWalletWithDefaultUsernameWhenEmailIsNull() {
    WalletDTO walletDTO = new WalletDTO();
    walletDTO.setEmail(null);

    WalletEntity walletEntity = new WalletEntity();
    UserEntity userEntity = UserEntity.builder().email(null).username("defaultUserName").build();
    WalletEntity savedWalletEntity = new WalletEntity();
    WalletDTO resultWalletDTO = new WalletDTO();

    when(walletMapper.toWalletEntity(walletDTO)).thenReturn(walletEntity);
    when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
    when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(userEntity);
    when(walletRepository.save(any(WalletEntity.class))).thenReturn(savedWalletEntity);
    when(walletMapper.toWalletDTO(savedWalletEntity)).thenReturn(resultWalletDTO);

    WalletDTO result = walletService.createWallet(walletDTO);

    assertNotNull(result);
    verify(userRepository).saveAndFlush(any(UserEntity.class));
    verify(walletRepository).save(any(WalletEntity.class));
    verify(walletMapper).toWalletDTO(savedWalletEntity);
  }

  @Test
  @DisplayName("should get wallet by id")
  void shouldGetWalletById() {
    Long walletId = 1L;
    WalletEntity walletEntity = new WalletEntity();
    WalletDTO walletDTO = new WalletDTO();

    when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
    when(walletMapper.toWalletDTO(walletEntity)).thenReturn(walletDTO);

    WalletDTO result = walletService.getWalletById(walletId);

    assertNotNull(result);
    verify(walletRepository).findById(walletId);
    verify(walletMapper).toWalletDTO(walletEntity);
  }

  @Test
  @DisplayName("should throw WalletNotFoundException when wallet not found")
  void shouldThrowWalletNotFoundExceptionWhenWalletNotFound() {
    Long walletId = 1L;
    when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

    assertThrows(WalletNotFoundException.class, () -> walletService.getWalletById(walletId));
  }
}
