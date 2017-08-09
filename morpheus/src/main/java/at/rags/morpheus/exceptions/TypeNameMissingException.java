package at.rags.morpheus.exceptions;

/**
 * Throws when {@link at.rags.morpheus.annotations.JsonApiType} is
 * not defined in a {@link at.rags.morpheus.Resource} class
 * @author Wuhao Ouyang.
 */

public class TypeNameMissingException extends RuntimeException {
    public TypeNameMissingException(Class<?> type) {
        super("Type not defined in " + type);
    }
}
