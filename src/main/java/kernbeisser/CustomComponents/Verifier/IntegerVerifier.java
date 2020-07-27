package kernbeisser.CustomComponents.Verifier;

import javax.swing.*;

public class IntegerVerifier extends ParseVerifier<Integer> {

  @Override
  public Integer parse(String s) {
    return Integer.parseInt(s);
  }

  public static InputVerifier from(int from, int to) {
    return new IntegerVerifier() {
      @Override
      public Integer max() {
        return to;
      }

      @Override
      public Integer min() {
        return from;
      }
    };
  }

  public static IntegerVerifier from(int min, int usualMin, int usualMax, int max) {
    return new IntegerVerifier() {
      @Override
      public Integer min() {
        return min;
      }

      @Override
      public Integer max() {
        return max;
      }

      @Override
      public Integer checkLowerThan() {
        return usualMin;
      }

      @Override
      public Integer checkHigherThan() {
        return usualMax;
      }
    };
  }
}
