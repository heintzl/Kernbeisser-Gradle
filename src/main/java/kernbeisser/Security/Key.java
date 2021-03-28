package kernbeisser.Security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import kernbeisser.Enums.PermissionKey;

/**
 * Annotation to specify which keys are required to run a function in a proxy secure instance {@link
 * Proxy#getSecureInstance(Object)} {@link Proxy#getSecuredInstances(Collection)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Key {
  PermissionKey[] value();
}
