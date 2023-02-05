package kernbeisser.Reports;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Reports.ReportDTO.TrialMemberReportEntry;
import kernbeisser.Useful.Date;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.Collections;

import static org.mockito.Mockito.*;

class TrialMemberReportTest {
    @Test
    void createOutFileName() {
        // arrange
        Instant now = Instant.now();
        Report report = new TrialMemberReport();

        // act
        String result = report.createOutFileName();

        // assert
        Assertions.assertEquals("Probemitlieder_" + Date.INSTANT_DATE.format(now), result);
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
            Report report = new TrialMemberReport();
            JasperPrint jspPrint = report.lazyGetJspPrint();

            // assert
            Assertions.assertNotNull(jspPrint);
            Assertions.assertEquals(ReportFileNames.TRIAL_MEMBER_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
        }
    }

    static void mockQuery(EntityManager entityManagerMock) {
        TypedQuery typedQueryTuple = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(TrialMemberReportEntry.class))).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultList()).thenReturn(Collections.emptyList());
    }

}