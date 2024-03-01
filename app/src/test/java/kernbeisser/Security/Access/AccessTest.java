package kernbeisser.Security.Access;

import static org.junit.jupiter.api.Assertions.*;

import kernbeisser.DBEntities.Article;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rs.groump.AccessDeniedException;
import rs.groump.AccessManager;

class AccessTest {

  @BeforeAll
  static void beforeAll() {
    Access.setDefaultManager(AccessManager.ACCESS_DENIED);
  }

  @Test
  void accessDeniedTest() {
    assertThrows(
        AccessDeniedException.class,
        () ->
            Access.runWithAccessManager(
                AccessManager.ACCESS_DENIED, () -> new Article().getName()));
  }

  @Test
  void accessGrantedTest() {
    assertDoesNotThrow(
        () ->
            Access.runWithAccessManager(
                AccessManager.NO_ACCESS_CHECKING, () -> new Article().getName()));
  }

  @Test
  void accessExceptionTest() {
    Article article = new Article();
    Access.runWithAccessManager(AccessManager.NO_ACCESS_CHECKING, () -> article.setId(42242));
    Access.putException(article, AccessManager.NO_ACCESS_CHECKING);
    assertDoesNotThrow(article::getName);
  }
}
