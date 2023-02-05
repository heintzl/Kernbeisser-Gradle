package kernbeisser.Reports;

import static org.mockito.Mockito.mockStatic;

import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class LoginInfoTest {

  @Test
  void createOutFileName() {
    // arrange
    User user = new User();
    user.setUsername("testaccount");
    Report report = new LoginInfo(user, null);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("loginInfo_" + "testaccount", result);
  }

  @Test
  void lazyGetJspPrint() {
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
      EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);

      User user = new User();
      user.setUsername("testaccount");

      // act
      Report report = new LoginInfo(user, null);
      JasperPrint jspPrint = report.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);
      Assertions.assertEquals("AnmeldeInformation", jspPrint.getName());
    }
  }
}
