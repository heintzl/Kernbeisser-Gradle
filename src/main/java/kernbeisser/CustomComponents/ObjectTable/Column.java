package kernbeisser.CustomComponents.ObjectTable;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Column <T> {
    String getName();
    Object getValue(T t);
    default void onAction(T t){}
    static <T> Column<T> create(String s, Function<T, Object> v){
        return new Column<T>() {
            @Override
            public String getName() {
                return s;
            }

            @Override
            public Object getValue(T t) {
                return v.apply(t);
            }
        };
    }
    static <T> Column<T> create(String s,Function<T,Object> v,Consumer<T> action){
        return new Column<T>() {
            @Override
            public String getName() {
                return s;
            }

            @Override
            public Object getValue(T t) {
                return v.apply(t);

            }

            @Override
            public void onAction(T t) {
                action.accept(t);
            }
        };
    }

    static <T> Column<T> createButton(String name, Function<T,String> value, Consumer<T> action){
        return new Column<T>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Object getValue(T t) {
                JButton button = new JButton(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action.accept(t);
                    }
                });
                button.setText(value.apply(t));
                return button;
            }
        };
    }

    static <T> Column<T> create(String s, Function<T,Object> v, Consumer<T> action, Key... keys){

        return new Column<T>() {
            boolean read;
            @Override
            public String getName() {
                read = LogInModel.getLoggedIn().hasPermission(keys);
                return s;
            }

            @Override
            public Object getValue(T t) {
                return read ? v.apply(t) : "***********";
            }

            @Override
            public void onAction(T t) {
                action.accept(t);
            }
        };
    }
    static <T> Column<T> create(String s, Function<T,Object> v, Key... keys){
        return new Column<T>() {
            boolean read;
            @Override
            public String getName() {
                read = LogInModel.getLoggedIn().hasPermission(keys);
                return s;
            }

            @Override
            public Object getValue(T t) {
                return read ? v.apply(t) : "***********";
            }
        };
    }
}
