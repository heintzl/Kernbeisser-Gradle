package kernbeisser.Windows.Inventory;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryViewTest {

    @Mock
    private InventoryView view;

    @Test
    void confirmPrint() {
        // arrange
        when(view.confirmPrint(anyString())).thenCallRealMethod();
        @NotNull JComponent mockComponent = new JComponent() {
        };
        when(view.getContent()).thenReturn(mockComponent);
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showOptionDialog(any(), any(), anyString(), anyInt(), anyInt(), any(), any(), any()))
                    .thenReturn(0);

            // act
            boolean result = view.confirmPrint("test message");

            // assert
            assertTrue(result);
            mockedPane.verify(() -> JOptionPane.showOptionDialog(mockComponent, "test message", "Achtung!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Ja", "Nein"}, "Nein"));

        }
    }
}