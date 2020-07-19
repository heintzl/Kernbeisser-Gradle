package kernbeisser.CustomComponents.Verifier;

public class KBNumberVerifier extends IntegerVerifier {
    @Override
    public Integer min() {
        return 0;
    }

    @Override
    public Integer max() {
        return 999999;
    }

}
