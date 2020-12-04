package kernbeisser.Windows.SynchronizeArticles;

import lombok.Getter;

public class Pair <T>{
  @Getter
  private final T a,b;

  public Pair(T a, T b) {
    this.a = a;
    this.b = b;
  }
}
