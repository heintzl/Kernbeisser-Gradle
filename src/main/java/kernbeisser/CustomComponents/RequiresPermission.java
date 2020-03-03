package kernbeisser.CustomComponents;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;

public interface RequiresPermission{

    void setReadable(boolean b);
    void setWriteable(boolean b);
    default void setRequiredKeys(Key read, Key write){
        setReadable(LogInModel.getLoggedIn().hasPermission(read));
        setWriteable(LogInModel.getLoggedIn().hasPermission(write));
    }

    default void setRequiredWriteKeys(Key ... keys){
        setWriteable(LogInModel.getLoggedIn().hasPermission(keys));
    }

    default void setRequiredReadKeys(Key ... keys){
        setReadable(LogInModel.getLoggedIn().hasPermission(keys));
    }
}
