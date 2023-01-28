package kernbeisser.Windows.Inventory.Counting;

import kernbeisser.Enums.Setting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountingControllerTest {
    @Mock
    private Setting inventoryScheduledDate;

    @Mock
    private CountingView view;
    @InjectMocks
    @Spy
    private CountingController controller;

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void setInventoryDateWarning(boolean inThePast) {
        // arrange
        when(inventoryScheduledDate.getDateValue()).thenReturn(inThePast ? LocalDate.parse("0868-05-11") : LocalDate.now());

        // act
        controller.setInventoryDateMessage(view);

        // assert
        verify(controller).createInventoryDateString(inThePast);
        verify(view).setInventoryDate(anyString());

        if (inThePast) {
            verify(view).formatInventoryDateAsWarning();
            verify(view, never()).formatInventoryDateAsMessage();
        } else {
            verify(view, never()).formatInventoryDateAsWarning();
            verify(view).formatInventoryDateAsMessage();
        }
    }

    @Test
    void createInventoryDateString_in_the_past() {
        // arrange
        when(inventoryScheduledDate.getDateValue()).thenReturn(LocalDate.parse("1234-12-27"));

        // act
        String result = controller.createInventoryDateString(true);

        // assert
        Assertions.assertEquals("Achtung: Das Inventur-Datum '27.12.1234' liegt in der Vergangenheit!", result);
    }

    @Test
    void createInventoryDateString() {
        // arrange
        when(inventoryScheduledDate.getDateValue()).thenReturn(LocalDate.parse("1234-12-27"));

        // act
        String result = controller.createInventoryDateString(false);

        // assert
        Assertions.assertEquals("Inventur-Datum: 27.12.1234", result);
    }
}