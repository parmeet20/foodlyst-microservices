package com.service.balanceService.service;

import com.service.balanceService.dto.CreateTransactionDto;
import com.service.balanceService.entities.Balance;
import com.service.balanceService.repo.BalanceRepository;
import com.service.balanceService.service.impl.BalanceServiceImpl;
import com.service.balanceService.service.kafka.kafkaProducerService.PublishPaymentToTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private PublishPaymentToTransaction publishPaymentToTransaction;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Balance userBalance1;
    private Balance userBalance2;

    @BeforeEach
    void setup() {
        userBalance1 = Balance.builder()
                .userId("user1")
                .balance(100.0)
                .walletConnected(true)
                .build();

        userBalance2 = Balance.builder()
                .userId("user2")
                .balance(50.0)
                .walletConnected(true)
                .build();
    }

    // ------------------------------------------------------
    // createWallet()
    // ------------------------------------------------------

    @Test
    void createWallet_ShouldCreateNewWallet_WhenNotExists() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.empty());

        balanceService.createWallet("user1");

        ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(captor.capture());
        Balance saved = captor.getValue();

        assertEquals("user1", saved.getUserId());
        assertEquals(0.0, saved.getBalance());
        assertTrue(saved.getWalletConnected());
    }

    @Test
    void createWallet_ShouldThrowException_WhenAlreadyExists() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> balanceService.createWallet("user1"));
        assertEquals("Wallet already exists for user: user1", ex.getMessage());
    }

    // ------------------------------------------------------
    // getBalanceByUserId()
    // ------------------------------------------------------

    @Test
    void getBalanceByUserId_ShouldReturnBalance_WhenExists() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));

        Balance result = balanceService.getBalanceByUserId("user1");

        assertEquals(100.0, result.getBalance());
        assertEquals("user1", result.getUserId());
    }

    @Test
    void getBalanceByUserId_ShouldThrowException_WhenNotFound() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> balanceService.getBalanceByUserId("user1"));
    }

    // ------------------------------------------------------
    // addFunds()
    // ------------------------------------------------------

    @Test
    void addFunds_ShouldIncreaseBalance() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance updated = balanceService.addFunds("user1", 50.0);

        assertEquals(150.0, updated.getBalance());
        verify(balanceRepository).save(updated);
    }

    // ------------------------------------------------------
    // deductFunds()
    // ------------------------------------------------------

    @Test
    void deductFunds_ShouldReduceBalance_WhenSufficient() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance updated = balanceService.deductFunds("user1", 40.0);

        assertEquals(60.0, updated.getBalance());
        verify(balanceRepository).save(updated);
    }

    @Test
    void deductFunds_ShouldThrowException_WhenInsufficient() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));

        assertThrows(RuntimeException.class,
                () -> balanceService.deductFunds("user1", 200.0));
        verify(balanceRepository, never()).save(any());
    }

    // ------------------------------------------------------
    // transferFunds()
    // ------------------------------------------------------

    @Test
    void transferFunds_ShouldTransfer_WhenSufficientBalance() throws Exception {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));
        when(balanceRepository.findByUserId("user2")).thenReturn(Optional.of(userBalance2));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = balanceService.transferFunds("user1", "user2", 50.0);

        assertTrue(result);
        assertEquals(50.0, userBalance1.getBalance());
        assertEquals(100.0, userBalance2.getBalance());
        verify(publishPaymentToTransaction).createTransactionEntry(any(CreateTransactionDto.class));
        verify(balanceRepository, times(2)).save(any(Balance.class));
    }

    @Test
    void transferFunds_ShouldThrowException_WhenInsufficientBalance() {
        userBalance1.setBalance(20.0);
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));
        when(balanceRepository.findByUserId("user2")).thenReturn(Optional.of(userBalance2));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> balanceService.transferFunds("user1", "user2", 50.0));

        assertEquals("Insufficient balance for transfer", ex.getMessage());
        verify(balanceRepository, never()).save(any());
    }

    // ------------------------------------------------------
    // connectWallet() / disconnectWallet()
    // ------------------------------------------------------

    @Test
    void connectWallet_ShouldSetWalletConnectedTrue() {
        userBalance1.setWalletConnected(false);
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.connectWallet("user1");

        assertTrue(result.getWalletConnected());
        verify(balanceRepository).save(result);
    }

    @Test
    void disconnectWallet_ShouldSetWalletConnectedFalse() {
        when(balanceRepository.findByUserId("user1")).thenReturn(Optional.of(userBalance1));
        when(balanceRepository.save(any(Balance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.disconnectWallet("user1");

        assertFalse(result.getWalletConnected());
        verify(balanceRepository).save(result);
    }
}