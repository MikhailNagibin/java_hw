package com.mipt.nagibinmikhail.hw10;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class MetricableDecorator implements DataService {
  private final DataService wrappedService;
  private final MetricService metricService;

  public MetricableDecorator(DataService wrappedService) {
    this.wrappedService = wrappedService;
    this.metricService = new MetricService();
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    Instant start = Instant.now();
    Optional<String> result = wrappedService.findDataByKey(key);
    metricService.sendMetric(Duration.between(start, Instant.now()));
    return result;
  }

  @Override
  public void saveData(String key, String data) {
    Instant start = Instant.now();
    wrappedService.saveData(key, data);
    metricService.sendMetric(Duration.between(start, Instant.now()));
  }

  @Override
  public boolean deleteData(String key) {
    Instant start = Instant.now();
    boolean result = wrappedService.deleteData(key);
    metricService.sendMetric(Duration.between(start, Instant.now()));
    return result;
  }

  public static class MetricService {
    public void sendMetric(Duration duration) {
      System.out.println("Метод выполнялся: " + duration.toString());
    }
  }
}

