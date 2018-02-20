package at.rags.morpheus;

import java.lang.reflect.Field;
import java.util.HashMap;

import at.rags.morpheus.exceptions.NotExtendingResourceException;

/**
 * Deserializer uses reflection to create objects and set fields.
 */
public class Deserializer {

    private static HashMap<String, Class> registeredClasses = new HashMap<>();

    /**
     * Register your class for a JSON type.
     * <p>
     * Example:
     * registerResourceClass("articles", Article.class);
     *
     * @param typeName      Name of the JSONAPI type.
     * @param resourceClass Class for mapping.
     * @see Resource
     */
    public static void registerResourceClass(String typeName, Class resourceClass) {
        registeredClasses.put(typeName, resourceClass);
    }

    /**
     * Creates an instance of an object via its name.
     *
     * @param resourceName Name of the resource.
     * @return Instance of the resourceName class.
     * @throws InstantiationException        Throws exception when not able to create instance of class.
     * @throws IllegalAccessException        Throws exception when not able to create instance of class.
     * @throws NotExtendingResourceException Throws exception when not able to create instance of class.
     */
    Resource createObjectFromString(String resourceName) throws InstantiationException, IllegalAccessException, NotExtendingResourceException {
        Class objectClass = registeredClasses.get(resourceName);
        if (objectClass == null) return null;
        try {
            return (Resource) objectClass.newInstance();
        } catch (InstantiationException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        } catch (ClassCastException e) {
            throw new NotExtendingResourceException(objectClass + " is not inheriting Resource");
        }
    }

    /**
     * Sets the field of the resourceObject with the data.
     *
     * @param resourceObject Object with field to be set.
     * @param fieldName      Name of the field.
     * @param data           Data to set.
     * @return Resource with or without field set
     */
    Resource setField(Resource resourceObject, String fieldName, Object data) {
        return setField(resourceObject, resourceObject.getClass(), fieldName, data);
    }

    /**
     * Sets the field of the resourceObject with the data.
     *
     * @param resourceObject Object with field to be set.
     * @param fieldName      Name of the field.
     * @param data           Data to set.
     * @return Resource with or without field set
     */
    Resource setField(Resource resourceObject, Class<?> objClass, String fieldName, Object data) {
        Field field = null;
        try {
            field = objClass.getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                field.set(resourceObject, data);
            } catch (IllegalAccessException e) {
                Logger.debug("Could not access " + field.getName() + " field");
            } catch (RuntimeException e) {
                Logger.debug("Could not set " + field.getName() + " field");
            } finally {
                field.setAccessible(accessible);
            }
        } catch (NoSuchFieldException e) {
            Logger.debug("Field " + fieldName + " not found.");
        }
        return resourceObject;
    }

    Object getRelationField(Resource resourceObject, String fieldName) {
        Field field = null;
        try {
            field = resourceObject.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                return field.get(resourceObject);
            } catch (IllegalAccessException e) {
                Logger.debug("Could not access " + field.getName() + " field");
            } catch (RuntimeException e) {
                Logger.debug("Could not set " + field.getName() + " field");
            } finally {
                field.setAccessible(accessible);
            }
        } catch (NoSuchFieldException e) {
            Logger.debug("Field " + fieldName + " not found.");
        }
        return null;
    }

    /**
     * Sets the Id field of the resourceObject extending {@link Resource}.
     *
     * @param resourceObject Object extending {@link Resource}.
     * @param data           Data with Id (as String or Int)
     * @return ResourceObject with set Id as String.
     * @throws NotExtendingResourceException when none of the superclasses are {@link Resource}.
     */
    Resource setIdField(Resource resourceObject, Object data) throws NotExtendingResourceException {
        Class superClass = null;
        try {
            superClass = getMorpheusResourceSuperClass(resourceObject);
        } catch (NotExtendingResourceException e) {
            throw e;
        }

        try {
            Field field = superClass.getDeclaredField("id");
            field.setAccessible(true);
            if (data instanceof String) {
                field.set(resourceObject, data);
            } else {
                field.set(resourceObject, String.valueOf(data));
            }
        } catch (NoSuchFieldException e) {
            Logger.debug("No field Id found. That should not happened.");
        } catch (IllegalAccessException e) {
            Logger.debug("Could not access field id");
        }

        return resourceObject;
    }

    Resource setTypeField(Resource resourceObject, Object data) {
        return setField(resourceObject, Resource.class, "type", data);
    }

    /**
     * Returns the superclass if instance of {@link Resource}.
     *
     * @param resourceObject Object to find the superclass.
     * @return {@link Resource} class.
     * @throws NotExtendingResourceException when resourceObject is not extending {@link Resource}.
     */
    private Class getMorpheusResourceSuperClass(Resource resourceObject) throws NotExtendingResourceException {
        Class superClass = resourceObject.getClass().getSuperclass();
        do {
            if (superClass == Resource.class) {
                break;
            }
            superClass = superClass.getSuperclass();
        } while (superClass != null);

        if (superClass == null) { //should not happen, cause createObjectFromString() checks
            throw new NotExtendingResourceException(resourceObject.getClass() + " is not inheriting Resource");
        }

        return superClass;
    }

    static HashMap<String, Class> getRegisteredClasses() {
        return registeredClasses;
    }

    static void setRegisteredClasses(HashMap<String, Class> registeredClasses) {
        at.rags.morpheus.Deserializer.registeredClasses = registeredClasses;
    }
}
