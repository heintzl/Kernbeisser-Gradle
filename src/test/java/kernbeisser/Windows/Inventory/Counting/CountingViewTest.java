package kernbeisser.Windows.Inventory.Counting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CountingViewTest {

    @InjectMocks
    private CountingView view;

    @Test
    void formatInventoryDateAsWarning() {
        // arrange
        view.inventoryDate.setForeground(Color.BLUE);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.FAMILY, Font.DIALOG);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        view.inventoryDate.setFont(Font.getFont(attributes));

        // act
        view.formatInventoryDateAsMessage();

        // assert
        Assertions.assertEquals(Color.BLACK, view.inventoryDate.getForeground());
        Assertions.assertEquals(TextAttribute.WEIGHT_SEMIBOLD, view.inventoryDate.getFont().getAttributes().get(TextAttribute.WEIGHT));
    }

    @Test
    void formatInventoryDateAsMessage() {
        // arrange
        view.inventoryDate.setForeground(Color.BLUE);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.FAMILY, Font.DIALOG);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        view.inventoryDate.setFont(Font.getFont(attributes));

        // act
        view.formatInventoryDateAsWarning();

        // assert
        Assertions.assertEquals(Color.RED, view.inventoryDate.getForeground());
        Assertions.assertEquals(TextAttribute.WEIGHT_EXTRABOLD, view.inventoryDate.getFont().getAttributes().get(TextAttribute.WEIGHT));
    }
}