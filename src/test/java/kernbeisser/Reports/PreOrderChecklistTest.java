package kernbeisser.Reports;

import kernbeisser.DBConnection.DBConnection;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.mockStatic;

class PreOrderChecklistTest {

    @Test
    void createOutFileName() {
        // arrange
        LocalDate now = LocalDate.now();
        Report report = new PreOrderChecklist(LocalDate.MIN, Collections.emptyList());

        // act
        String result = report.createOutFileName();

        // assert
        Assertions.assertEquals("preOrderChecklist" + now, result);
    }

    @Test
    void lazyGetJspPrint() {

        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            // arrange
            EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
            EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
            EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);

            // act
            Report report = new PreOrderChecklist(LocalDate.MIN, Collections.emptyList());
            JasperPrint jspPrint = report.lazyGetJspPrint();

            // assert
            Assertions.assertNotNull(jspPrint);
            Assertions.assertEquals(ReportFileNames.PREORDER_CHECKLIST_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
        }
    }

}