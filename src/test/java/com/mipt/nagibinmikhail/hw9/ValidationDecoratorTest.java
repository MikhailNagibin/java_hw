package com.mipt.nagibinmikhail.hw9;

import com.mipt.nagibinmikhail.hw10.DataService;
import com.mipt.nagibinmikhail.hw10.SimpleDataService;
import com.mipt.nagibinmikhail.hw10.ValidationDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationDecoratorTest {
  private ValidationDecorator validationService;

  @BeforeEach
  void setUp() {
    DataService simpleService = new SimpleDataService();
    validationService = new ValidationDecorator(simpleService);
  }

  @Test
  void testFindDataByKey_ValidatesKey() {
    assertThrows(IllegalArgumentException.class, () -> validationService.findDataByKey(null));
    assertThrows(IllegalArgumentException.class, () -> validationService.findDataByKey(""));
    assertThrows(IllegalArgumentException.class, () -> validationService.findDataByKey("   "));

    String longKey = "a".repeat(101);
    assertThrows(IllegalArgumentException.class, () -> validationService.findDataByKey(longKey));
  }

  @Test
  void testSaveData_ValidatesInput() {
    assertThrows(IllegalArgumentException.class, () -> validationService.saveData(null, "data"));
    assertThrows(IllegalArgumentException.class, () -> validationService.saveData("", "data"));
    assertThrows(IllegalArgumentException.class, () -> validationService.saveData("key", null));

    String longData = "a".repeat(10001);
    assertThrows(IllegalArgumentException.class, () -> validationService.saveData("key", longData));
  }

  @Test
  void testDeleteData_ValidatesKey() {
    assertThrows(IllegalArgumentException.class, () -> validationService.deleteData(null));
    assertThrows(IllegalArgumentException.class, () -> validationService.deleteData(""));
    assertThrows(IllegalArgumentException.class, () -> validationService.deleteData("   "));
  }

  @Test
  void testValidInput_WorksCorrectly() {
    assertDoesNotThrow(() -> {
      validationService.saveData("validKey", "validData");
      Optional<String> result = validationService.findDataByKey("validKey");
      boolean deleted = validationService.deleteData("validKey");

      assertTrue(result.isPresent());
      assertEquals("validData", result.get());
      assertTrue(deleted);
    });
  }
}