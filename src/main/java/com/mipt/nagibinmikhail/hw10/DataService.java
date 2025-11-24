package com.mipt.nagibinmikhail.hw10;

import java.util.*;

public interface DataService {
  Optional<String> findDataByKey(String key);

  void saveData(String key, String data);

  boolean deleteData(String key);
}