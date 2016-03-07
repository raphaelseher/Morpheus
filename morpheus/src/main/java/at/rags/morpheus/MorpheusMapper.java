package at.rags.morpheus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import at.rags.morpheus.Annotations.Relationship;
import at.rags.morpheus.Annotations.SerializeName;

/**
 * Created by raphaelseher on 07/03/16.
 */
public class MorpheusMapper {

  public MorpheusMapper() {
  }

  /**
   * Deserializes a json object of data to the registered class.
   *
   * @param dataObject JSONObject from data
   * @return Deserialized Object.
   */
  public Object mapDataObject(JSONObject dataObject) {
    Object realObject = null;

    try {
      realObject = MorpheusDeserializer.createObjectFromString(getTypeFromJson(dataObject));
    } catch (Exception e) {
      Logger.debug(e.getMessage());
    }

    realObject = mapId(realObject, dataObject);

    try {
      realObject = mapAttributes(realObject, dataObject.getJSONObject("attributes"));
    } catch (Exception e) {
      Logger.debug("JSON data does not contain attributes");
    }

    try {
      realObject = mapRelations(realObject, dataObject.getJSONObject("relationships"));
    } catch (Exception e) {
      Logger.debug("JSON data does not contain relationships");
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
   * @see #mapDataObject(JSONObject)
   */
  public List<Object> mapDataArray(JSONArray dataArray) {
    ArrayList<Object> objects = new ArrayList<>();

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject jsonObject = null;

      try {
        jsonObject = dataArray.getJSONObject(i);
      } catch (JSONException e) {
        Logger.debug("Was not able to get dataArray["+i+"] as JSONObject.");
      }
      objects.add(mapDataObject(jsonObject));
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
  public Object mapId(Object object, JSONObject jsonDataObject) {
    try {
      return MorpheusDeserializer.setIdField(object, jsonDataObject.get("id"));
    } catch (JSONException e) {
      Logger.debug("JSON data does not contain id.");
    } catch (Exception e) {
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
  public Object mapAttributes(Object object, JSONObject attributesJsonObject) {

    for (Field field : object.getClass().getDeclaredFields()) {
      // get the right attribute name
      String jsonFieldName = field.getName();
      boolean isRelation = false;
      for (Annotation annotation : field.getAnnotations()) {
        if (annotation.annotationType() == SerializeName.class) {
          SerializeName serializeName = (SerializeName) annotation;
          jsonFieldName = serializeName.jsonName();
        }
        if (annotation.annotationType() == Relationship.class) {
          isRelation = true;
        }
      }

      if (!isRelation) {
        try {
          MorpheusDeserializer.setField(object, jsonFieldName, attributesJsonObject.get(jsonFieldName));
        } catch (JSONException e) {
          Logger.debug("JSON attributes does not contain " + jsonFieldName);
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
  public Object mapRelations(Object object, JSONObject jsonObject) throws Exception {
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
        Object relationObject = mapDataObject(relationDataObject);

        MorpheusDeserializer.setField(object, relationship, relationObject);
      } catch (JSONException e) {
        Logger.debug("JSON relationship does not contain data");
      }

      //map json array of data
      JSONArray relationDataArray = null;
      try {
        relationDataArray = relationJsonObject.getJSONArray("data");
        List<Object> relationArray = mapDataArray(relationDataArray);

        MorpheusDeserializer.setField(object, relationship, relationArray);
      } catch (JSONException e) {
        Logger.debug("JSON relationship does not contain data");
      }
    }

    return object;
  }g

  // helper

  /**
   * Get the type of the data message.
   *
   * @param object JSONObject.
   * @return Name of the json type.
   */
  private String getTypeFromJson(JSONObject object) {
    String type = null;
    try {
      type = object.getString("type");
    } catch (JSONException e) {
      Logger.debug("JSON data does not contain type");
    }
    return type;
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
}
