package com.mipt.nagibinmikhail.hw7;

import java.util.*;


class Student {
  private int id;
  private String name;
  private double grade;

  public Student(int id, String name, double grade) {
    this.id = id;
    this.name = name;
    this.grade = grade;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Student student = (Student) o;
    return id == student.id &&
      Double.compare(student.grade, grade) == 0 &&
      Objects.equals(name, student.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, grade);
  }

  public int getId() {
    return id;
  }

  public double getGrade() {
    return grade;
  }

  public String getName() {
    return name;
  }
}


public class task3 {
  public static void main(String[] args) {
    var test1 = new HashMap<Integer, Student>();
    test1.put(1, new Student(1, "Alice", 4.5));
    test1.put(2, new Student(2, "Bob", 3.8));
    test1.put(3, new Student(3, "Charlie", 4.2));

    var test2 = new TreeMap<Integer, Student>(Collections.reverseOrder());
    test2.put(1, new Student(1, "Alice", 4.5));
    test2.put(2, new Student(2, "Bob", 3.8));
    test2.put(3, new Student(3, "Charlie", 4.2));
  }
}


final class SomeMethods {
  private  SomeMethods() {};

  public static List<Student> findStudentsByGradeRange(Map<Integer, Student> map, double minGrade, double maxGrade) {
    var answer = new ArrayList<Student>();
    for (Student currentStudent: map.values()) {
      if (currentStudent.getGrade() > minGrade && currentStudent.getGrade() < maxGrade) {
        answer.add(currentStudent);
      }
    }
    return answer;
  }

  public static List<Student> getTopNStudents(TreeMap<Integer, Student> map, int n) {
    var answer = new ArrayList<Student>();
    int count = 0;
    for (Student currentStudent: map.values()) {
      if (count > n) {
        break;
      }
      answer.add(currentStudent);
    }
    return answer;
  }
}