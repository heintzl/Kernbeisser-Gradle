package kernbeisser.CustomComponents.Verifier;

public class LongVerifier extends ParseVerifier<Long>{

    @Override
    public Long parse(String s) throws NumberFormatException {
        return Long.parseLong(s);
    }

    @Override
    public Long min() {
        return Long.MIN_VALUE;
    }

    @Override
    public Long max() {
        return Long.MAX_VALUE;
    }

}
