package com.service.balanceService.controller;

import com.service.balanceService.entities.Balance;
import com.service.balanceService.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createWallet(@PathVariable String userId) {
        try {
            balanceService.createWallet(userId);
            return new ResponseEntity<>("Wallet created successfully for user: " + userId, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable String userId) {
        try {
            Balance balance = balanceService.getBalanceByUserId(userId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addFunds(@PathVariable String userId, @RequestParam double amount) {
        try {
            if (amount <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            Balance balance = balanceService.addFunds(userId, amount);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/deduct/{userId}")
    public ResponseEntity<?> deductFunds(@PathVariable String userId, @RequestParam double amount) {
        try {
            if (amount <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            Balance balance = balanceService.deductFunds(userId, amount);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@RequestParam String fromUserId,
                                           @RequestParam String toUserId,
                                           @RequestParam double amount) {
        try {
            if (amount <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            boolean success = balanceService.transferFunds(fromUserId, toUserId, amount);
            return new ResponseEntity<>("Transfer successful", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/connect/{userId}")
    public ResponseEntity<?> connectWallet(@PathVariable String userId) {
        try {
            Balance balance = balanceService.connectWallet(userId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/disconnect/{userId}")
    public ResponseEntity<?> disconnectWallet(@PathVariable String userId) {
        try {
            Balance balance = balanceService.disconnectWallet(userId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}