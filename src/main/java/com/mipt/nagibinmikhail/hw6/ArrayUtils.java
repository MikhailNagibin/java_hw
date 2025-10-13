package com.mipt.nagibinmikhail.hw6;

public class ArrayUtils {
  public static <T> int findFirst(T[] array, T element) {
    if (array == null) {
      return -1;
    }

    for (int i = 0; i < array.length; i++) {
      if (array[i] == null && element == null) {
        return i;
      }
      if (array[i].equals(element)) {
        return i;
      }
    }
    return -1;
  }

  public static void main(String[] args) {
    // пример использования
    final String[] names = {null, "Bob", null};
    final int index = ArrayUtils.findFirst(names, null); // Ожидаем: 1 (тк нумерация в массиве начинается с нуля)
    System.out.println(index);
  }
}
