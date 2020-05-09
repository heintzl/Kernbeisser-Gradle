package kernbeisser.CustomComponents.Verifier;

public class KBNumberVerifier extends IntegerVerifier{
    @Override
    public Integer min() {
        return 0;
    }

    @Override
    public Integer max() {
        return 999999;
    }

    @Override
    public String message() {
        return "Die Artikelnummer darf nur zwischen 0 und 999999 liegen";
    }
}
