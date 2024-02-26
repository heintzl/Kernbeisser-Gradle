package kernbeisser.Security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import rs.groump.PermissionKey;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Key {
  PermissionKey[] value();

  // gets generated by the agent -> the id of the access checking call for tracing back original
  // method
  long id() default 0;
}
