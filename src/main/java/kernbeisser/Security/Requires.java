package kernbeisser.Security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kernbeisser.Enums.KeyCollection;
import kernbeisser.Enums.PermissionKey;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Requires {
  PermissionKey[] value();

  KeyCollection[] collections() default {};
}
