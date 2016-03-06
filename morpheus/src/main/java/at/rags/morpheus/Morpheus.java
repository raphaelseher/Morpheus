package at.rags.morpheus;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.rags.morpheus.Annotations.Relationship;
import at.rags.morpheus.Annotations.SerializeName;

/**
 * Work in progress.
 */
public class Morpheus {
  private static final String TAG = Morpheus.class.getSimpleName();

  private ArrayMap<String, Class> mRegisteredClasses;

  @TargetApi(Build.VERSION_CODES.KITKAT)
  public Morpheus() {
    mRegisteredClasses = new ArrayMap<>();
  }

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
  public void registerResourceClass(String typeName, Class resourceClass) {
    mRegisteredClasses.put(typeName, resourceClass);
  }

  public JSONAPIObject jsonToObject(String jsonString) throws Exception {
    JSONAPIObject jsonapiObject = new JSONAPIObject();
    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(jsonString);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Invalid JSON String.");
    }

    //included
    JSONArray includedArray = null;
    try {
      includedArray = jsonObject.getJSONArray("included");
      jsonapiObject.setIncluded(deserializeDataNode(includedArray));
    } catch (JSONException e) {
      Log.d(TAG, "included array not found");
    }

    //data array
    JSONArray dataArray = null;
    try {
      dataArray = jsonObject.getJSONArray("data");
      jsonapiObject.setResources(deserializeDataNode(dataArray));
    } catch (JSONException e) {
      Log.d(TAG, "data array not found");
    }

    //data object
    JSONObject dataObject = null;
    try {
      dataObject = jsonObject.getJSONObject("data");
      jsonapiObject.setResource(deserializeDataNode(dataObject));
    } catch (JSONException e) {
      Log.d(TAG, "data object not found");
    }

    //TODO map included on relation
    //TODO map links
    //TODO errors
    //TODO meta

    return jsonapiObject;
  }

  /**
   * Deserializes a json object of data to the registered class.
   *
   * @param dataObject JSONObject from data
   * @return Deserialized Object.
   */
  private Object deserializeDataNode(JSONObject dataObject) {
    Object realObject = null;

    try {
      realObject = createObjectFromString(dataObject.getString("type"));
    } catch (Exception e) {
      Log.d(TAG, "type field not found in json");
    }

    try {
      realObject = mapId(realObject, dataObject);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      realObject = mapAttributes(realObject, dataObject.getJSONObject("attributes"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      realObject = mapRelations(realObject, dataObject.getJSONObject("relationships"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    //TODO map links
    //TODO meta

    return realObject;
  }

  /**
   * Loops through data objects and deserializes them.
   *
   * @param dataArray JSONArray of the data node.
   * @return List of deserialized objects.
   * @see #deserializeDataNode(JSONObject)
   */
  private List<Object> deserializeDataNode(JSONArray dataArray) {
    ArrayList<Object> objects = new ArrayList<>();

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject jsonObject = null;

      try {
        jsonObject = dataArray.getJSONObject(i);
        objects.add(deserializeDataNode(jsonObject));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return objects;
  }

  /**
   * Map the Id from json to the object.
   *
   * @param object Object of the class.
   * @param jsonDataObject JSONObject of the dataNode.
   * @return Object with mapped fields.
   * @throws Exception
   */
  private Object mapId(Object object, JSONObject jsonDataObject) throws Exception {
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
      Field idField = superClass.getDeclaredField("Id");
      idField.setAccessible(true);
      idField.set(object, jsonDataObject.get("id"));
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    return object;
  }

  /**
   * Maps the attributes of json to the object.
   *
   * @param object Object of the class.
   * @param attributesJsonObject Attributes object inside the data node.
   * @return Object with mapped fields.
   */
  private Object mapAttributes(Object object, JSONObject attributesJsonObject) throws Exception {
    for (Field field : object.getClass().getDeclaredFields()) {

      // get the right attribute name
      String jsonFieldName = field.getName();
      for (Annotation annotation : field.getAnnotations()) {
        if (annotation.annotationType() == SerializeName.class) {
          SerializeName serializeName = (SerializeName) annotation;
          jsonFieldName = serializeName.jsonName();

          //set the field with the data from the json
          try {
            field.setAccessible(true);
            field.set(object, attributesJsonObject.get(jsonFieldName));
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (JSONException e) {
            throw new Exception("Cannot find " + jsonFieldName + " in JSON");
          }
        }
      }
    }

    return object;
  }

  /**
   * Loops through relation JSON array and maps annotated objects.
   *
   * @param object Real object to map.
   * @param jsonObject JSONObject.
   * @return Real object with relations.
   */
  private Object mapRelations(Object object, JSONObject jsonObject) throws Exception {
    List<Object> relations = new ArrayList<>();

    List<String> relationshipNames = getRelationshipNames(object.getClass());

    //going through relationship names annotated in Class
    for (String relationship : relationshipNames) {
      JSONObject relationJsonObject = null;
      try {
        relationJsonObject = jsonObject.getJSONObject(relationship);

      } catch (JSONException e) {
        throw new Exception("Relationship named " + relationship + "not found in JSON");
      }

      //map json object of data
      JSONObject relationDataObject = null;
      try {
        relationDataObject = relationJsonObject.getJSONObject("data");
        Object relationObject = deserializeDataNode(relationDataObject);

        Field relationField = object.getClass().getDeclaredField(relationship);
        relationField.setAccessible(true);
        relationField.set(object, relationObject);
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }

      //map json array of data
      JSONArray relationDataArray = null;
      try {
        relationDataArray = relationJsonObject.getJSONArray("data");
        List<Object> relationArray = deserializeDataNode(relationDataArray);

        Field relationField = object.getClass().getDeclaredField(relationship);
        relationField.setAccessible(true);
        relationField.set(object, relationArray);
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }

    }

    return object;
  }

  /**
   * Get the root data node object as array.
   *
   * @param object JSONObject.
   * @return JSONArray from data node.
   */
  private JSONArray getDataObjects(JSONObject object) {
    JSONArray dataArray = null;
    try {
      dataArray = object.getJSONArray("data");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return dataArray;
  }

  /**
   * Get the annotated relationship names.
   *
   * @param clazz Class for annotation.
   * @return List of relationship names.
   */
  private  List<String> getRelationshipNames(Class clazz) {
    List<String> relationNames = new ArrayList<>();
    for (Field field : clazz.getDeclaredFields()) {
      for (Annotation annotation : field.getDeclaredAnnotations()) {
        if (annotation.annotationType() == Relationship.class) {
          Relationship relationshipAnnotation = (Relationship)annotation;
          relationNames.add(relationshipAnnotation.relationName());
        }
      }
    }

    return relationNames;
  }

  /**
   * Creates an instance of an object via its name.
   *
   * @param resourceName Name of the resource.
   * @return Instance of the resourceName class.
   * @throws Exception
   */
  private Object createObjectFromString(String resourceName) throws Exception {
    Class objectClass = mRegisteredClasses.get(resourceName);
    try {
      return objectClass.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
      throw new Exception("Unable to create new instance of " + objectClass);
    }
  }

  public ArrayMap<String, Class> getRegisteredClasses() {
    return mRegisteredClasses;
  }
}
