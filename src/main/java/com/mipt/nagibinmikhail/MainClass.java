package com.mipt.nagibinmikhail;

public class MainClass {
  private int privateInt;
  private String privateString;
  protected static double protectedStaticDouble;
  public final long publicFinalLong = 1000;

  public static void main(String[] args) {
    for (int i = 0; i <= 15; i++) {
      System.out.println("Iter: " + i);
    }
  }
}