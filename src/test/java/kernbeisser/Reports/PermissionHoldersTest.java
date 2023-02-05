package kernbeisser.Reports;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;

class PermissionHoldersTest {

    @Test
    void createOutFileName() {
        // arrange
        LocalDate now = LocalDate.now();
        Report report = new PermissionHolders(true);

        // act
        String result = report.createOutFileName();

        // assert
        Assertions.assertEquals("RollenInhaber" + now, result);
    }

    @Test
    void lazyGetJspPrint() {
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            // arrange
            EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
            EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
            EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);
            mockQuery(entityManagerMock);

            // act
            Report report = new PermissionHolders(true);
            JasperPrint jspPrint = report.lazyGetJspPrint();

            // assert
            Assertions.assertNotNull(jspPrint);
            Assertions.assertEquals(ReportFileNames.PERMISSION_HOLDERS_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
        }
    }

    static void mockQuery(EntityManager entityManagerMock) {
        TypedQuery typedQueryTuple = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(Permission.class))).thenReturn(typedQueryTuple);
        when(typedQueryTuple.setParameter(anyString(), any())).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultList()).thenReturn(Collections.emptyList());
    }


}