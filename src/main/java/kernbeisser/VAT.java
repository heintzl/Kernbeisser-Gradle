package kernbeisser;

import java.util.function.Supplier;

public enum VAT implements Supplier<Integer> {
    LOW{
        @Override
        public Integer get() {
            return 7;
        }
    },
    HIGH{
        @Override
        public Integer get() {
            return 19;
        }
    }
}
