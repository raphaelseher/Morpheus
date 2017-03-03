package at.rags.morpheus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Define your json:api type.
 * @author wuhaoouyang
 */
public @interface JsonApiType {
    String value();
}
