package com.service.balanceService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.balanceService.controller.BalanceController;
import com.service.balanceService.entities.Balance;
import com.service.balanceService.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceController balanceController;

    private Balance sampleBalance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();
        ObjectMapper objectMapper = new ObjectMapper();

        sampleBalance = Balance.builder()
                .userId("user1")
                .balance(100.0)
                .walletConnected(true)
                .build();
    }

    // -------------------------------------------------------
    // createWallet()
    // -------------------------------------------------------

    @Test
    void createWallet_ShouldReturn201_WhenWalletCreated() throws Exception {
        doNothing().when(balanceService).createWallet("user1");
        mockMvc.perform(post("/api/v1/wallet/create/user1"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Wallet created successfully for user: user1"));
        verify(balanceService).createWallet("user1");
    }

    @Test
    void createWallet_ShouldReturn400_WhenWalletExists() throws Exception {
        doThrow(new RuntimeException("Wallet already exists for user: user1"))
                .when(balanceService).createWallet("user1");

        mockMvc.perform(post("/api/v1/wallet/create/user1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wallet already exists for user: user1"));
    }

    // -------------------------------------------------------
    // getBalance()
    // -------------------------------------------------------

    @Test
    void getBalance_ShouldReturn200_WhenWalletFound() throws Exception {
        when(balanceService.getBalanceByUserId("user1")).thenReturn(sampleBalance);

        mockMvc.perform(get("/api/v1/wallet/balance/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.walletConnected").value(true));
    }

    @Test
    void getBalance_ShouldReturn404_WhenWalletNotFound() throws Exception {
        when(balanceService.getBalanceByUserId("user1"))
                .thenThrow(new RuntimeException("Wallet not found for user: user1"));

        mockMvc.perform(get("/api/v1/wallet/balance/user1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found for user: user1"));
    }

    // -------------------------------------------------------
    // addFunds()
    // -------------------------------------------------------

    @Test
    void addFunds_ShouldReturn200_WhenSuccess() throws Exception {
        when(balanceService.addFunds("user1", 50.0))
                .thenReturn(Balance.builder().userId("user1").balance(150.0).walletConnected(true).build());

        mockMvc.perform(post("/api/v1/wallet/add/user1")
                        .param("amount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.0));
    }

    @Test
    void addFunds_ShouldReturn400_WhenAmountIsNonPositive() throws Exception {
        mockMvc.perform(post("/api/v1/wallet/add/user1")
                        .param("amount", "-10.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount must be positive"));

        verify(balanceService, never()).addFunds(anyString(), anyDouble());
    }

    @Test
    void addFunds_ShouldReturn400_WhenServiceThrowsException() throws Exception {
        when(balanceService.addFunds("user1", 50.0))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/v1/wallet/add/user1")
                        .param("amount", "50.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    // -------------------------------------------------------
    // deductFunds()
    // -------------------------------------------------------

    @Test
    void deductFunds_ShouldReturn200_WhenSuccess() throws Exception {
        when(balanceService.deductFunds("user1", 50.0))
                .thenReturn(Balance.builder().userId("user1").balance(50.0).walletConnected(true).build());

        mockMvc.perform(post("/api/v1/wallet/deduct/user1")
                        .param("amount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50.0));
    }

    @Test
    void deductFunds_ShouldReturn400_WhenAmountInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/wallet/deduct/user1")
                        .param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount must be positive"));
    }

    @Test
    void deductFunds_ShouldReturn400_WhenServiceThrowsException() throws Exception {
        when(balanceService.deductFunds("user1", 10.0))
                .thenThrow(new RuntimeException("Insufficient funds"));

        mockMvc.perform(post("/api/v1/wallet/deduct/user1")
                        .param("amount", "10.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient funds"));
    }

    // -------------------------------------------------------
    // transferFunds()
    // -------------------------------------------------------

    @Test
    void transferFunds_ShouldReturn200_WhenSuccess() throws Exception {
        when(balanceService.transferFunds("user1", "user2", 30.0)).thenReturn(true);

        mockMvc.perform(post("/api/v1/wallet/transfer")
                        .param("fromUserId", "user1")
                        .param("toUserId", "user2")
                        .param("amount", "30.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));
    }

    @Test
    void transferFunds_ShouldReturn400_WhenAmountInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/wallet/transfer")
                        .param("fromUserId", "user1")
                        .param("toUserId", "user2")
                        .param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount must be positive"));
    }

    @Test
    void transferFunds_ShouldReturn400_WhenServiceThrowsException() throws Exception {
        when(balanceService.transferFunds("user1", "user2", 30.0))
                .thenThrow(new RuntimeException("Insufficient balance for transfer"));

        mockMvc.perform(post("/api/v1/wallet/transfer")
                        .param("fromUserId", "user1")
                        .param("toUserId", "user2")
                        .param("amount", "30.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance for transfer"));
    }

    // -------------------------------------------------------
    // connectWallet() / disconnectWallet()
    // -------------------------------------------------------

    @Test
    void connectWallet_ShouldReturn200_WhenSuccess() throws Exception {
        when(balanceService.connectWallet("user1")).thenReturn(sampleBalance);

        mockMvc.perform(put("/api/v1/wallet/connect/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.walletConnected").value(true));
    }

    @Test
    void connectWallet_ShouldReturn400_WhenServiceThrowsException() throws Exception {
        when(balanceService.connectWallet("user1"))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/api/v1/wallet/connect/user1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void disconnectWallet_ShouldReturn200_WhenSuccess() throws Exception {
        Balance disconnected = Balance.builder()
                .userId("user1")
                .balance(100.0)
                .walletConnected(false)
                .build();

        when(balanceService.disconnectWallet("user1")).thenReturn(disconnected);

        mockMvc.perform(put("/api/v1/wallet/disconnect/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletConnected").value(false));
    }

    @Test
    void disconnectWallet_ShouldReturn400_WhenServiceThrowsException() throws Exception {
        when(balanceService.disconnectWallet("user1"))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/api/v1/wallet/disconnect/user1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }
}