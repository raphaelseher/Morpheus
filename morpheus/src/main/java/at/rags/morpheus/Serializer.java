package at.rags.morpheus;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import at.rags.morpheus.annotations.Relationship;

/**
 */
public class Serializer {

  /**
   * Return objects fields as dictionary with fieldName as key
   * and fieldObject as value.
   *
   * @param resource A morpheus resource.
   * @return hashMap of field names and values.
   */
  public HashMap<String, Object> getFieldsAsDictionary(Resource resource) {
    HashMap<String, Object> fieldDict = null;

    for (Field field : resource.getClass().getDeclaredFields()) {
      String fieldName = null;

      if (field.isAnnotationPresent(Relationship.class)) {
        continue;
      }

      Object fieldValue = null;
      try {
        field.setAccessible(true);
        fieldValue = field.get(resource);
        if (fieldValue == null) {
          continue;
        }
      } catch (IllegalAccessException e) {
        Logger.debug("Cannot access field: " + fieldName + ".");
      }

      if (field.isAnnotationPresent(SerializedName.class)) {
        Annotation annotation = field.getAnnotation(SerializedName.class);
        SerializedName serializeName = (SerializedName) annotation;
        fieldName = serializeName.value();
      } else {
        fieldName = field.getName();
      }

      if (fieldDict == null) {
        fieldDict = new HashMap<>();
      }

      fieldDict.put(fieldName, fieldValue);
    }

    return fieldDict;
  }

  public HashMap<String, Object> getRelationships(Resource resource) {
    HashMap<String, Object> relationships = new HashMap<>();

    for (Field field : resource.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Relationship.class)) {
        Annotation annotation = field.getAnnotation(Relationship.class);
        Relationship relationship = (Relationship) annotation;

        field.setAccessible(true);
        try {
          relationships.put(relationship.value(), field.get(resource));
        } catch (IllegalAccessException e) {
          Logger.debug("Cannot access field: " + field.getName() + ".");
        }
      }
    }

    return relationships;
  }
}
