package kernbeisser.Useful;

import java.util.List;
import kernbeisser.Security.Utils.Setter;

public class ObjectInputReader<T> {

  private List<Setter<T, String>> columns;

  public T putDataInto(T base, String[] source) {
    for (int i = 0; i < source.length; i++) {
      columns.get(i).set(base, source[i]);
    }
    return base;
  }

  public ObjectInputReader<T> column(Setter<T, String> stringSetter) {
    columns.add(stringSetter);
    return this;
  }

  public ObjectInputReader<T> columnInt(Setter<T, Integer> stringSetter) {
    columns.add((p, v) -> stringSetter.set(p, Integer.parseInt(v)));
    return this;
  }

  public ObjectInputReader<T> columnDouble(Setter<T, Double> stringSetter) {
    columns.add((p, v) -> stringSetter.set(p, Double.parseDouble(v.replace(",", "."))));
    return this;
  }

  public <E extends Enum<E>> ObjectInputReader<T> columnEnum(Setter<T, E> setter, Class<E> eEnum) {
    columns.add((p, v) -> setter.set(p, Enum.valueOf(eEnum, v)));
    return this;
  }

  public ObjectInputReader<T> emptyColumn(String columnName) {
    columns.add(unusedColumn());
    return this;
  }

  private Setter<T, String> unusedColumn() {
    return (v, k) -> {};
  }

  public static <T> Setter<T, String> parse(Setter<T, Integer> base) {
    return (p, v) -> base.set(p, Integer.parseInt(v));
  }
}
