package at.rags.morpheus.Annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * Define a json:api attribute name, if it differs from the field name.
 *
 * <pre>
 * {@code
 * @SerializeName("post-title")
 * private String title;
 * }
 * </pre>
 */
public @interface SerializeName {
  String value();
}
