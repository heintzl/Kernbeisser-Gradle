package kernbeisser.CustomComponents.Verifier;

public class DoubleVerifier extends ParseVerifier<Double> {
  public static DoubleVerifier from(double from, double to) {
    return new DoubleVerifier() {
      @Override
      public Double min() {
        return from;
      }

      @Override
      public Double max() {
        return to;
      }
    };
  }

  public static DoubleVerifier from(double min, double usualMin, double usualMax, double max) {
    return new DoubleVerifier() {
      @Override
      public Double min() {
        return min;
      }

      @Override
      public Double max() {
        return max;
      }

      @Override
      public Double checkLowerThan() {
        return usualMin;
      }

      @Override
      public Double checkHigherThan() {
        return usualMax;
      }
    };
  }

  public Double min() {
    return Double.NEGATIVE_INFINITY;
  }

  public Double max() {
    return Double.POSITIVE_INFINITY;
  }

  @Override
  public Double parse(String s) {
    return Double.parseDouble(s.replace(",", "."));
  }
}
