package kernbeisser.Security.Access;

import static org.junit.jupiter.api.Assertions.*;

import kernbeisser.DBEntities.Article;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rs.groump.Access;
import rs.groump.AccessDeniedException;
import rs.groump.AccessManager;

class AccessTest {

  @BeforeAll
  static void beforeAll() {
    Access.setAccessManager(AccessManager.ACCESS_DENIED);
  }

  @Test
  void accessDeniedTest() {
    Access.runWithAccessManager(
        AccessManager.ACCESS_GRANTED,
        () -> {
          assertThrows(
              AccessDeniedException.class,
              () ->
                  Access.runWithAccessManager(
                      AccessManager.ACCESS_DENIED, () -> new Article().getName()));
        });
  }

  @Test
  void accessGrantedTest() {
    assertDoesNotThrow(
        () ->
            Access.runWithAccessManager(
                AccessManager.ACCESS_GRANTED, () -> new Article().getName()));
    assertDoesNotThrow(() -> Access.runUnchecked(() -> new Article().getName()));
  }
}
