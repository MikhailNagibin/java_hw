package com.mipt.nagibinmikhail.hw9;

import com.mipt.nagibinmikhail.hw10.CachingDecorator;
import com.mipt.nagibinmikhail.hw10.DataService;
import com.mipt.nagibinmikhail.hw10.SimpleDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

class CachingDecoratorTest {
  private DataService cachingService;
  private DataService mockService;

  @BeforeEach
  void setUp() {
    mockService = new SimpleDataService();
    cachingService = new CachingDecorator(mockService);
  }

  @Test
  void testFindDataByKey_CachesResult() {
    cachingService.saveData("key1", "data1");

    Optional<String> result1 = cachingService.findDataByKey("key1");

    Optional<String> result2 = cachingService.findDataByKey("key1");

    assertTrue(result1.isPresent());
    assertEquals("data1", result1.get());
    assertTrue(result2.isPresent());
    assertEquals("data1", result2.get());
  }

  @Test
  void testSaveData_UpdatesCache() {
    cachingService.saveData("key1", "data1");

    cachingService.saveData("key1", "newData");
    Optional<String> result = cachingService.findDataByKey("key1");

    assertTrue(result.isPresent());
    assertEquals("newData", result.get());
  }

  @Test
  void testDeleteData_InvalidatesCache() {
    cachingService.saveData("key1", "data1");
    cachingService.findDataByKey("key1"); // Заполняем кэш

    boolean deleted = cachingService.deleteData("key1");
    Optional<String> result = cachingService.findDataByKey("key1");

    assertTrue(deleted);
    assertFalse(result.isPresent());
  }
}



