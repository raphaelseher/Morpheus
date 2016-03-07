package at.rags.morpheus;

import android.util.ArrayMap;

import java.lang.reflect.Field;

/**
 * Created by raphaelseher on 07/03/16.
 */
public class MorpheusDeserializer {

  private static ArrayMap<String, Class> mRegisteredClasses = new ArrayMap<>();

  /**
   * Register your class for a JSON type.
   *
   * Example:
   * registerResourceClass("articles", Article.class);
   *
   * @param typeName Name of the JSONAPI type.
   * @param resourceClass Class for mapping.
   * @see MorpheusResource
   */
  public static void registerResourceClass(String typeName, Class resourceClass) {
    mRegisteredClasses.put(typeName, resourceClass);
  }

  /**
   * Creates an instance of an object via its name.
   *
   * @param resourceName Name of the resource.
   * @return Instance of the resourceName class.
   * @throws Exception
   */
  public static Object createObjectFromString(String resourceName) throws Exception {
    Class objectClass = mRegisteredClasses.get(resourceName);
    try {
      return objectClass.newInstance();
    } catch (InstantiationException e) {
      throw new Exception("Unable to create new instance of " + objectClass);
    }
  }

  public static Object setField(Object object, String fieldName, Object data) {
    Field field = null;
    try {
      field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(object, data);
    } catch (NoSuchFieldException e) {
      Logger.debug("Field " + fieldName + " not found.");
    } catch (IllegalAccessException e) {
      Logger.debug("Could not access " + field.getName() + " field");
    }

    return object;
  }

  public static Object setIdField(Object object, Object data) throws Exception {
    Class superClass = object.getClass().getSuperclass();
    do {
      if (superClass == MorpheusResource.class) {
        break;
      }
      superClass = superClass.getSuperclass();
    } while (superClass != null);

    if (superClass == null) {
      throw new Exception(object.getClass() + " is not inheriting MorpheusResource");
    }

    try {
      Field field = superClass.getDeclaredField("Id");
      field.setAccessible(true);
      field.set(object, data);
    } catch (NoSuchFieldException e) {
      Logger.debug("No field Id found. That should not happened.");
    } catch (IllegalAccessException e) {
      Logger.debug("Could not access field Id");
    }

    return object;
  }

}
