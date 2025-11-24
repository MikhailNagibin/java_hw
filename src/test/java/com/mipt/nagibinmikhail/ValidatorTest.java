package com.mipt.nagibinmikhail;
import com.mipt.nagibinmikhail.hw8.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

  static class TestUser {
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Email(message = "Invalid email format")
    @NotNull(message = "Email cannot be null")
    private String email;

    @Range(min = 0, max = 150, message = "Age must be between 0 and 150")
    private Integer age;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;


    public TestUser() {}

    public TestUser(String name, String email, Integer age, String password) {
      this.name = name;
      this.email = email;
      this.age = age;
      this.password = password;
    }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAge(Integer age) { this.age = age; }
    public void setPassword(String password) { this.password = password; }
  }

  @Test
  void testSuccessfulValidation() {
    TestUser user = new TestUser("John Doe", "john@example.com", 25, "securepassword");
    ValidationResult result = Validator.validate(user);

    assertTrue(result.isValid());
    assertEquals(0, result.getErrors().size());
  }

  @Test
  void testNotNullValidation() {
    TestUser user = new TestUser();
    user.setName(null);
    user.setEmail(null);
    user.setAge(25);
    user.setPassword("password");

    ValidationResult result = Validator.validate(user);

    assertFalse(result.isValid());
    assertEquals(2, result.getErrors().size());
    assertTrue(result.getErrors().contains("Name cannot be null"));
    assertTrue(result.getErrors().contains("Email cannot be null"));
  }

  @Test
  void testSizeValidation() {
    TestUser user = new TestUser();
    user.setName("A");
    user.setEmail("valid@example.com");
    user.setAge(25);
    user.setPassword("short");

    ValidationResult result = Validator.validate(user);
    assertFalse(result.isValid());
    assertEquals(2, result.getErrors().size());
    assertTrue(result.getErrors().contains("Name must be between 2 and 50 characters"));
    assertTrue(result.getErrors().contains("Password must be between 6 and 20 characters"));
  }

  @Test
  void testRangeValidation() {
    TestUser user = new TestUser();
    user.setName("Valid Name");
    user.setEmail("valid@example.com");
    user.setAge(-5);
    user.setPassword("validpassword");

    ValidationResult result = Validator.validate(user);

    assertFalse(result.isValid());
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().contains("Age must be between 0 and 150"));
  }

  @Test
  void testEmailValidation() {
    TestUser user = new TestUser();
    user.setName("Valid Name");
    user.setEmail("invalid-email");
    user.setAge(25);
    user.setPassword("password");

    ValidationResult result = Validator.validate(user);
    assertFalse(result.isValid());
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().contains("Invalid email format"));
  }

  @Test
  void testMultipleErrors() {
    TestUser user = new TestUser();
    user.setName(null);
    user.setEmail("invalid");
    user.setAge(200);
    user.setPassword("short");

    ValidationResult result = Validator.validate(user);

    assertFalse(result.isValid());
    assertEquals(4, result.getErrors().size());
  }

  @Test
  void testNullObject() {
    ValidationResult result = Validator.validate(null);

    assertFalse(result.isValid());
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().contains("object can not be null"));
  }

  @Test
  void testEdgeCases() {
    TestUser user1 = new TestUser("Jo", "test@example.com", 25, "exact6"); // мин. длина
    ValidationResult result1 = Validator.validate(user1);
    assertTrue(result1.isValid());

    TestUser user2 = new TestUser("John", "test@example.com", 0, "password"); // мин. возраст
    ValidationResult result2 = Validator.validate(user2);
    assertTrue(result2.isValid());

    TestUser user3 = new TestUser("John", "test@example.com", 150, "password"); // макс. возраст
    ValidationResult result3 = Validator.validate(user3);
    assertTrue(result3.isValid());
  }
}