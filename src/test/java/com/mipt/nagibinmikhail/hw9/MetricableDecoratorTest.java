package com.mipt.nagibinmikhail.hw9;

import com.mipt.nagibinmikhail.hw10.DataService;
import com.mipt.nagibinmikhail.hw10.MetricableDecorator;
import com.mipt.nagibinmikhail.hw10.SimpleDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricableDecoratorTest {
  private MetricableDecorator metricService;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  @BeforeEach
  void setUp() {
    DataService simpleService = new SimpleDataService();
    metricService = new MetricableDecorator(simpleService);
    System.setOut(new PrintStream(outputStream));
  }

  @Test
  void testFindDataByKey_SendsMetric() {
    metricService.saveData("testKey", "testData");
    outputStream.reset();

    metricService.findDataByKey("testKey");

    String output = outputStream.toString();
    assertTrue(output.contains("Метод выполнялся: PT"));
  }

  @Test
  void testSaveData_SendsMetric() {
    outputStream.reset();

    metricService.saveData("testKey", "testData");

    String output = outputStream.toString();
    assertTrue(output.contains("Метод выполнялся: PT"));
  }

  @Test
  void testDeleteData_SendsMetric() {
    metricService.saveData("testKey", "testData");
    outputStream.reset();

    metricService.deleteData("testKey");

    String output = outputStream.toString();
    assertTrue(output.contains("Метод выполнялся: PT"));
  }
}
