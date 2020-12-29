package kernbeisser.Security.StaticMethodTransformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited

//creates a static callable function which has no reference to the current object its invoked on
public @interface StaticAccessPoint {



}
