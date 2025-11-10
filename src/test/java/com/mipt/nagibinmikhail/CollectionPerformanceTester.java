package com.mipt.nagibinmikhail;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionPerformanceTester {

  private static final int ELEMENT_COUNT = 10000;
  private static final int TEST_ITERATIONS = 10;

  @Test
  void testCollectionPerformance() {
    System.out.println();
    System.out.printf("%-20s | %-12s | %-12s \n",
      "Операция", "ArrayList   ", "LinkedList    ");
    System.out.println("---------------------|--------------|-----------");

    warmUp();

    testOperation("Добавление в конец", this::testAddToEnd);
    testOperation("Добавление в начало", this::testAddToStart);
    testOperation("Вставка в середину", this::testInsertToMiddle);
    testOperation("Доступ по индексу", this::testAccessByIndex);
    testOperation("Удаление из начала", this::testRemoveFromStart);
    testOperation("Удаление из конца", this::testRemoveFromEnd);
  }

  private void warmUp() {
    testAddToEnd(new ArrayList<>());
    testAddToEnd(new LinkedList<>());
  }

  private void testOperation(String operationName, TestFunction testFunction) {
    long arrayListTotalTime = 0;
    long linkedListTotalTime = 0;

    for (int i = 0; i < TEST_ITERATIONS; i++) {
      arrayListTotalTime += testFunction.test(new ArrayList<>());
      linkedListTotalTime += testFunction.test(new LinkedList<>());
    }

    double arrayListAvgTime = arrayListTotalTime / (double) TEST_ITERATIONS / 1_000.0; // в микросекундах
    double linkedListAvgTime = linkedListTotalTime / (double) TEST_ITERATIONS / 1_000.0;

    System.out.printf("%-20s | %-12.2f | %-12.2f \n",
      operationName, arrayListAvgTime, linkedListAvgTime);
  }

  @FunctionalInterface
  private interface TestFunction {
    long test(List<Integer> list);
  }

  // Реализации методов остаются такими же, как в предыдущем примере
  private long testAddToEnd(List<Integer> list) {
    long startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }
    return System.nanoTime() - startTime;
  }

  private long testAddToStart(List<Integer> list) {
    long startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(0, i);
    }
    return System.nanoTime() - startTime;
  }

  private long testInsertToMiddle(List<Integer> list) {
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }
    long startTime = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      list.add(list.size() / 2, i);
    }
    return System.nanoTime() - startTime;
  }

  private long testAccessByIndex(List<Integer> list) {
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }
    long startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      int value = list.get(i);
    }
    return System.nanoTime() - startTime;
  }

  private long testRemoveFromStart(List<Integer> list) {
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }
    long startTime = System.nanoTime();
    while (!list.isEmpty()) {
      list.remove(0);
    }
    return System.nanoTime() - startTime;
  }

  private long testRemoveFromEnd(List<Integer> list) {
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }
    long startTime = System.nanoTime();
    while (!list.isEmpty()) {
      list.remove(list.size() - 1);
    }
    return System.nanoTime() - startTime;
  }
}