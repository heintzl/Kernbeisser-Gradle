package kernbeisser.Security.IterableProtection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kernbeisser.Enums.PermissionKey;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyIterable {
  PermissionKey[] read();

  PermissionKey[] modify();
}
