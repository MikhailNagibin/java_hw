package com.mipt.nagibinmikhail.hw11;

public class BankAccount {
  private final int id;
  private int balance;
  private final String accountName;

  public BankAccount(int id, String accountName) {
    this.id = id;
    this.balance = 0;
    this.accountName = accountName;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public int getId() {
    return id;
  }

  public String getAccountName() {
    return accountName;
  }
}