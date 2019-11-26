package kernbeisser.Windows;


import javax.swing.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface View {
    Controller getController();
    default <T extends Window> void open(Supplier<? super JFrame> w){

    }
}
