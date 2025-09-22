package com.mipt.nagibinmikhail.model;

public class Human {
  private String firstName;
  private String lastName;
  private int age;
  private boolean isWorking;


  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public int getAge() { return age; }
  public void setAge(int age) { this.age = age; }

  public boolean isWorking() { return isWorking; }
  public void setWorking(boolean working) { isWorking = working; }
}