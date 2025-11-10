package com.mipt.nagibinmikhail.hw6;

public class Calculator<T extends Number> {
  private double[] toDouble(T a, T b) {
    double doubleA;
    double doubleB;
    if (a == null) {
      doubleA = 0;
    } else {
      doubleA = a.doubleValue();
    }
    if (b == null) {
      doubleB = 0;
    } else {
      doubleB = b.doubleValue();
    }
    return new double[]{doubleA, doubleB};
  }

  public double sum(T a, T b) {
    double[] doubles = toDouble(a, b);
    return doubles[1] + doubles[0];
  }

  public double subtract(T a, T b) {
    double[] doubles = toDouble(a, b);
    return doubles[0] - doubles[1];
  }

  private double multiply(T a, T b) {
    double[] doubles = toDouble(a, b);
    return doubles[0] * doubles[1];
  }

  private double divide(T a, T b) {
    double[] doubles = toDouble(a, b);
    if (doubles[1] == 0) {
      return Double.NaN;
    }
    return doubles[0] / doubles[1];
  }
  // реализуйте остальные методы согласно требованиям

  public static void main(String[] args) {
    // пример использования
    final Calculator<Integer> intCalc = new Calculator<>();
    final double result = intCalc.sum(5, 3); // 8.0

    final Calculator<Double> doubleCalc = new Calculator<>();
    final double div = doubleCalc.divide(10.0, 4.0); // 2.5
  }
}