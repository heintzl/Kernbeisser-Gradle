package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.NotNull;

public class ObjectForm <P>{

    private final Bounded<P,?>[] boundedFields;

    @SafeVarargs
    public ObjectForm(Bounded<P,?> ... boundedFields) {
        this.boundedFields = boundedFields;
    }

    public P pasteDataInto(@NotNull P pattern) throws CannotParseException{
        for (Bounded<P,?> boundedField : boundedFields) {
            try {
                if(boundedField.canWrite(pattern))
                boundedField.writeInto(pattern);
            } catch (CannotParseException e) {
                throw new CannotParseException();
            }
        }
        return pattern;
    }

    public P ignoreWrongInput(@NotNull P pattern){
        for (Bounded<P,?> boundedField : boundedFields) {
            try {
                boundedField.writeInto(pattern);
            } catch (CannotParseException ignored) {
            }
        }
        return pattern;
    }

    public void setData(@NotNull P data) {
        for (Bounded<P,?> boundedField : boundedFields) {
            boundedField.setObjectData(data);
        }
    }

    public void refreshAccess(@NotNull P data){
        P accessModel = Tools.clone(data);
        for (Bounded<P,?> boundedField : boundedFields) {
            boundedField.setReadable(boundedField.canRead(accessModel));
            boundedField.setWriteable(boundedField.canWrite(accessModel));
        }
    }

    public void markErrors(){
        for (Bounded<P,?> field : boundedFields) {
            if (!field.validInput()) {
                    field.markWrongInput();
            }
        }
    }
}
