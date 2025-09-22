package com.mipt.nagibinmikhail;

public abstract class WorkerAbstractClass {
  public abstract void work(int hours);

  public boolean goHome(String firstString, String secondString) {
    return firstString.equals(secondString);
  }
}