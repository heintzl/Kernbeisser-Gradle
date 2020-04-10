package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Exeptions.IncorrectInput;

public class IntegerParseField extends FilterField<Integer> {
    IntegerParseField(int min, int max) {
        super(e -> {
            try {
                if (e.equals("")||e.equals("-")) {
                    return 0;
                }
                int v = Integer.parseInt(e.replace(",","."));
                if(v <= min || v >= max)throw new IncorrectInput("integer "+v+" is not between min: "+min+" and max: "+max);
                return v;
            } catch (NumberFormatException ex) {
                throw new IncorrectInput("cannot parse integer from "+e);
            }
        });
    }

    public IntegerParseField() {
        this(Integer.MIN_VALUE,Integer.MAX_VALUE);
    }
}
