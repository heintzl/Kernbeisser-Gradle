package kernbeisser.CustomComponents.ObjectTable;

import java.util.function.Function;

public interface Column <T> {
    String getName();
    String getValue(T t);
    static<T> Column<T> create(String s, Function<T,Object> v){
        return new Column<T>() {
            @Override
            public String getName() {
                return s;
            }

            @Override
            public String getValue(T t) {
                return v.apply(t).toString();
            }
        };
    }
}
