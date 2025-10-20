package com.mipt.nagibinmikhail.hw7;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomArrayList<A> implements CustomList<A>, Iterable<A> {
  private Object[] array;
  private int size;
  private int capacity = 2;

  public CustomArrayList() {
    array = new Object[capacity];
    size = 0;
  }

  private void extendCapacity() {
    capacity = Math.max((int)(capacity * 1.5), capacity + 1);
    Object[] newElements = new Object[capacity];
    for (int i = 0; i < size; i++) {
      newElements[i] = array[i];
    }
    array = newElements;
  }

  @Override
  public void add(A element) {
    if (element == null) {
      throw new IllegalArgumentException("Element cannot be null");
    }
    if (size >= capacity) {
      extendCapacity();
    }
    array[size] = element;
    size++;
  }

  @Override
  @SuppressWarnings("unchecked")
  public A get(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return (A) array[index];
  }

  @Override
  @SuppressWarnings("unchecked")
  public A remove(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    A removedElement = (A) array[index];

    for (int i = index; i < size - 1; i++) {
      array[i] = array[i + 1];
    }
    array[size - 1] = null;
    size--;

    return removedElement;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public Iterator<A> iterator() {
    return new CustomArrayListIterator();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < size; i++) {
      result.append(array[i].toString());
      if (i < size - 1) {
        result.append(" ");
      }
    }
    return result.toString();
  }

  private class CustomArrayListIterator implements Iterator<A> {
    private int cursor = 0;
    private int lastReturned = -1;
    private boolean removeAllowed = false;

    @Override
    public boolean hasNext() {
      return cursor < size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public A next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      lastReturned = cursor;
      cursor++;
      removeAllowed = true;
      return (A) array[lastReturned];
    }

    @Override
    public void remove() {
      if (!removeAllowed) {
        throw new IllegalStateException("next() must be called before remove()");
      }

      CustomArrayList.this.remove(lastReturned);
      cursor = lastReturned; // Возвращаем курсор на позицию удаленного элемента
      removeAllowed = false; // Запрещаем повторный вызов remove()
    }
  }
}