package com.mipt.nagibinmikhail.hw10;

import java.util.Optional;

public class ValidationDecorator implements DataService {
  private final DataService wrappedService;

  public ValidationDecorator(DataService wrappedService) {
    this.wrappedService = wrappedService;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    validateKey(key);
    return wrappedService.findDataByKey(key);
  }

  @Override
  public void saveData(String key, String data) {
    validateKey(key);
    validateData(data);
    wrappedService.saveData(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    validateKey(key);
    return wrappedService.deleteData(key);
  }

  private void validateKey(String key) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("Ключ не может быть пустым или null");
    }
    if (key.length() > 100) {
      throw new IllegalArgumentException("Ключ не может быть длиннее 100 символов");
    }
  }

  private void validateData(String data) {
    if (data == null) {
      throw new IllegalArgumentException("Данные не могут быть null");
    }
    if (data.length() > 10000) {
      throw new IllegalArgumentException("Данные не могут быть длиннее 10000 символов");
    }
  }
}