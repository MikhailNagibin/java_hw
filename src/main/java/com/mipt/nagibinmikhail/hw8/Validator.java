package com.mipt.nagibinmikhail.hw8;
import java.lang.reflect.Field;

public class Validator {
  public static ValidationResult validate(Object object) {
    ValidationResult result = new ValidationResult();

    if (object == null) {
      result.addError("object can not be null");
      return result;
    }

    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);

      try {
        Object value = field.get(object);

        if (field.isAnnotationPresent(NotNull.class)) {
          NotNull notNull = field.getAnnotation(NotNull.class);
          if (value == null) {
            result.addError(notNull.message());
          }
        }
        if (field.isAnnotationPresent(Size.class)) {
          Size size = field.getAnnotation(Size.class);
          if (value instanceof String) {
            String stringValue = (String) value;
            if (stringValue.length() < size.min() || stringValue.length() > size.max()) {
              result.addError(size.message());
            }
          }
        }
        if (field.isAnnotationPresent(Range.class)) {
          Range range = field.getAnnotation(Range.class);
          if (value instanceof Number) {
            Number numberValue = (Number) value;
            long longValue = numberValue.longValue();
            if (longValue < range.min() || longValue > range.max()) {
              result.addError(range.message());
            }
          }
        }
        if (field.isAnnotationPresent(Email.class)) {
          Email email = field.getAnnotation(Email.class);
          if (value instanceof String) {
            String emailValue = (String) value;
            if (!isValidEmail(emailValue)) {
              result.addError(email.message());
            }
          }
        }

      } catch (IllegalAccessException e) {
        result.addError(field.getName());
      }
    }

    return result;
  }

  private static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }
    boolean was_dog = false;
    boolean was_dot = false;
    for (int i = 1; i < email.length() - 1; i++) {
      if (email.charAt(i) == '@') {
        was_dog = true;
      } else if (was_dog && email.charAt(i - 1) != '@' && email.charAt(i) == '.') {
        was_dot = true;
      }
    }
    return was_dog & was_dot;
  }
}