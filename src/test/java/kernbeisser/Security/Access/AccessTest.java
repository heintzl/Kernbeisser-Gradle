package kernbeisser.Security.Access;

import static org.junit.jupiter.api.Assertions.*;

import kernbeisser.DBEntities.Article;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AccessTest {

  @BeforeAll
  static void beforeAll() {
    Access.setDefaultManager(AccessManager.ACCESS_DENIED);
  }

  @Test
  void accessDeniedTest() {
    assertThrows(
        PermissionKeyRequiredException.class,
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
