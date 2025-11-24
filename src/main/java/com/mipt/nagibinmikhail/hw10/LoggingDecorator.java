package com.mipt.nagibinmikhail.hw10;

import java.util.Optional;

public class LoggingDecorator implements DataService {
  private final DataService wrappedService;

  public LoggingDecorator(DataService wrappedService) {
    this.wrappedService = wrappedService;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    System.out.println("Поиск данных по ключу: " + key);
    Optional<String> result = wrappedService.findDataByKey(key);
    System.out.println("Результат поиска: " + (result.isPresent() ? "найден" : "не найден"));
    return result;
  }

  @Override
  public void saveData(String key, String data) {
    System.out.println("Сохранение данных. Ключ: " + key + ", Данные: " + data);
    wrappedService.saveData(key, data);
    System.out.println("Данные сохранены успешно");
  }

  @Override
  public boolean deleteData(String key) {
    System.out.println("Удаление данных по ключу: " + key);
    boolean result = wrappedService.deleteData(key);
    System.out.println("Результат удаления: " + (result ? "успешно" : "ключ не найден"));
    return result;
  }
}
