package com.mipt.nagibinmikhail.hw10;

import com.mipt.nagibinmikhail.hw11.BankAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.*;

import com.mipt.nagibinmikhail.hw11.Bank;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

  private Bank bank;
  private BankAccount account1;
  private BankAccount account2;

  @BeforeEach
  void setUp() {
    bank = new Bank();
    account1 = new BankAccount(1, "Account 1");
    account2 = new BankAccount(2, "Account 2");
  }

  @Test
  void testBasicTransfer() {
    account1.setBalance(1000);
    bank.sendToAccount(account1, account2, 300);

    assertEquals(700, account1.getBalance());
    assertEquals(300, account2.getBalance());
  }

  @Test
  void testInsufficientFundsThrowsException() {
    account1.setBalance(50);

    assertThrows(IllegalArgumentException.class, () -> {
      bank.sendToAccount(account1, account2, 100);
    });
  }

  @Test
  void testNullAccountValidation() {
    assertThrows(IllegalArgumentException.class, () -> {
      bank.sendToAccount(null, account2, 100);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      bank.sendToAccount(account1, null, 100);
    });
  }

  @Test
  void testInvalidAmount() {
    account1.setBalance(1000);

    assertThrows(IllegalArgumentException.class, () -> {
      bank.sendToAccount(account1, account2, -100);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      bank.sendToAccount(account1, account2, 0);
    });
  }

  @Test
  void testZeroAmount() {
    account1.setBalance(1000);

    assertThrows(IllegalArgumentException.class, () -> {
      bank.sendToAccount(account1, account2, 0);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      bank.sendToAccount(account1, account2, 0);
    });
  }

  @Test
  void testDeadlockScenario() throws InterruptedException {
    account1.setBalance(1000);
    account2.setBalance(1000);

    Thread thread1 = new Thread(() -> {
      bank.sendToAccountDeadlock(account1, account2, 100);
    });

    Thread thread2 = new Thread(() -> {
      bank.sendToAccountDeadlock(account2, account1, 100);
    });

    thread1.start();
    thread2.start();

    thread1.join(2000);
    thread2.join(2000);

    if (thread1.isAlive() || thread2.isAlive()) {
      thread1.interrupt();
      thread2.interrupt();
    }

    int totalBalance = account1.getBalance() + account2.getBalance();
    assertEquals(2000, totalBalance);
  }

  @Test
  void testTransferToSelf() {
    account1.setBalance(1000);
    bank.sendToAccount(account1, account1, 500);
    assertEquals(1000, account1.getBalance());
  }

  @Test
  void testTransferAllFunds() {
    account1.setBalance(300);
    bank.sendToAccount(account1, account2, 300);

    assertEquals(0, account1.getBalance());
    assertEquals(300, account2.getBalance());
  }
}