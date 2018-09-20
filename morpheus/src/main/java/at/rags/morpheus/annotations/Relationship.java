package at.rags.morpheus.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * Define your json:api relationship.
 *
 * <pre>
 * {@code
 * @Relationship("author")
 * private Author author;
 *
 * @Relationship("comments")
 * private List<Comment> comments;
 * }
 * </pre>
 */
public @interface Relationship {
  String value();
}