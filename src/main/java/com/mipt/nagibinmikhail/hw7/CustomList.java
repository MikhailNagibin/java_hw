package com.mipt.nagibinmikhail.hw7;

import java.util.Iterator;

public interface CustomList<A> {
  /*
   * appends new element to the end
   *
   * @param element element to be appanded to list
   * @throws IllegalArgumentException if the specified element is null
   */
  void add(A element);

  /*
   * return element by his index
   *
   * @param index index of the returned item
   * @throws ArrayIndexOutOfBoundsException if index negative or more than size
   * @returns element with the required index
   */
  A get(int index);

  /*
   * remove element by his index
   *
   * @param index index of the item being deleted
   * @throws ArrayIndexOutOfBoundsException if index negative or more than size
   * @returns removed element
   */
  A remove(int index);

  /*
   * return count of elements
   *
   * @returns length of list
   */
  int size();

  /*
   * are there any items in the list
   *
   * @returns true if list is empty else false
   */
  boolean isEmpty();

  Iterator<A> iterator();
}
