package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.CannotParseException;

public class ObjectForm <P>{


    private final Bounded<P>[] boundedFields;

    @SafeVarargs
    public ObjectForm(Bounded<P> ... boundedFields) {
        this.boundedFields = boundedFields;
    }

    public P pasteDataInto(P pattern) throws CannotParseException{
        for (Bounded<P> boundedField : boundedFields) {
            try {
                boundedField.putOn(pattern);
            } catch (CannotParseException e) {
                throw new CannotParseException();
            }
        }
        return pattern;
    }

    public P ignoreWrongInput(P pattern){
        for (Bounded<P> boundedField : boundedFields) {
            try {
                boundedField.putOn(pattern);
            } catch (CannotParseException ignored) {
            }
        }
        return pattern;
    }

    public void setData(P data) {
        for (Bounded<P> boundedField : boundedFields) {
            boundedField.setValue(data);
        }
    }
}
