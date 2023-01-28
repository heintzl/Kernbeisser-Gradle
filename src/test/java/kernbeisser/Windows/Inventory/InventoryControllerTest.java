package kernbeisser.Windows.Inventory;

import kernbeisser.Enums.Setting;
import kernbeisser.Reports.InventoryCountingLists;
import kernbeisser.Reports.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {
    @Mock
    private Setting inventoryScheduledDate;
    @Mock
    private InventoryView view;

    @InjectMocks
    private InventoryController controller;

    MockedConstruction<InventoryCountingLists> mockedConstruction;

    @BeforeEach
    void init() {
        mockedConstruction = mockConstruction(InventoryCountingLists.class);
    }

    @AfterEach
    void destroy() {
        mockedConstruction.close();
    }

    @Test
    void print_countingList_with_user_query() {
        when(inventoryScheduledDate.getDateValue()).thenReturn(LocalDate.parse("1234-12-27"));
        when(view.confirmPrint(anyString())).thenReturn(true);

        // act
        controller.print(InventoryReports.COUNTINGLISTS, true, true);

        // assert
        verify(view).confirmPrint("Das Inventur-Datum '27.12.1234' liegt in der Vergangenheit. Wirklich drucken?");
        assertThat(mockedConstruction.constructed()).hasSize(1);

        Report report = mockedConstruction.constructed().get(0);
        verify(report).exportPdf(anyString(), any());
        verify(report, never()).sendToPrinter(anyString(), any());
    }

    @Test
    void print_countingList_with_user_query_denied() {
        when(inventoryScheduledDate.getDateValue()).thenReturn(LocalDate.parse("1234-12-27"));
        when(view.confirmPrint(anyString())).thenReturn(false);

        // act
        controller.print(InventoryReports.COUNTINGLISTS, true, true);

        // assert
        verify(view).confirmPrint("Das Inventur-Datum '27.12.1234' liegt in der Vergangenheit. Wirklich drucken?");
        assertThat(mockedConstruction.constructed()).hasSize(0);
    }

    @ParameterizedTest
    @EnumSource(value = InventoryReports.class, mode = EnumSource.Mode.EXCLUDE, names = "COUNTINGLISTS")
    void print_reports_without_user_query(InventoryReports report) {
        when(inventoryScheduledDate.getDateValue()).thenReturn(LocalDate.parse("1234-12-27"));

        // act
        controller.print(report, true, true);

        // assert
        verify(view, never()).confirmPrint(anyString());
    }

}