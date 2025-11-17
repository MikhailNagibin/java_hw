package com.mipt.nagibinmikhail.hw9;

import com.mipt.nagibinmikhail.hw10.DataService;
import com.mipt.nagibinmikhail.hw10.LoggingDecorator;
import com.mipt.nagibinmikhail.hw10.SimpleDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingDecoratorTest {
  private LoggingDecorator loggingService;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  @BeforeEach
  void setUp() {
    DataService simpleService = new SimpleDataService();
    loggingService = new LoggingDecorator(simpleService);
    System.setOut(new PrintStream(outputStream));
  }

  @Test
  void testFindDataByKey_LogsAction() {
    loggingService.saveData("testKey", "testData");
    outputStream.reset();

    loggingService.findDataByKey("testKey");

    String output = outputStream.toString();
    assertTrue(output.contains("Поиск данных по ключу: testKey"));
    assertTrue(output.contains("Результат поиска: найден"));
  }

  @Test
  void testSaveData_LogsAction() {
    outputStream.reset();

    loggingService.saveData("testKey", "testData");

    String output = outputStream.toString();
    assertTrue(output.contains("Сохранение данных. Ключ: testKey, Данные: testData"));
    assertTrue(output.contains("Данные сохранены успешно"));
  }

  @Test
  void testDeleteData_LogsAction() {
    loggingService.saveData("testKey", "testData");
    outputStream.reset();

    loggingService.deleteData("testKey");

    String output = outputStream.toString();
    assertTrue(output.contains("Удаление данных по ключу: testKey"));
    assertTrue(output.contains("Результат удаления: успешно"));
  }
}
