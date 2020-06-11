package kernbeisser.Security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Key {

    kernbeisser.Enums.Key[] value();
}
