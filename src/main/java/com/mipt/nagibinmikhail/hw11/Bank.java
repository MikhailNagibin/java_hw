package com.mipt.nagibinmikhail.hw11;

public class Bank {
  public void sendToAccountDeadlock(BankAccount from, BankAccount to, int amount) {
    if (from == null || to == null || amount <= 0) {
      throw new IllegalArgumentException();
    }

    synchronized (from) {
      try {
        Thread.sleep(3);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      synchronized (to) {
        if (from.getBalance() >= amount) {
          from.setBalance(from.getBalance() - amount);
          to.setBalance(to.getBalance() + amount);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  public void sendToAccount(BankAccount from, BankAccount to, int amount) {
    if (from == null || to == null || amount <= 0) {
      throw new IllegalArgumentException();
    }

    BankAccount firstLock = from.getId() < to.getId() ? from : to;
    BankAccount secondLock = from.getId() < to.getId() ? to : from;

    synchronized (firstLock) {
      synchronized (secondLock) {
        if (from.getBalance() >= amount) {
          from.setBalance(from.getBalance() - amount);
          to.setBalance(to.getBalance() + amount);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }
  }
}
