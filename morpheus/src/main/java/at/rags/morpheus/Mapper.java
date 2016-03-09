package at.rags.morpheus;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.rags.morpheus.Annotations.Relationship;
import at.rags.morpheus.Annotations.SerializeName;

/**
 * Created by raphaelseher on 07/03/16.
 */
public class Mapper {

  private Deserializer mDeserializer;

  public Mapper() {
    mDeserializer = new Deserializer();
  }

  public Mapper(Deserializer deserializer) {
    mDeserializer = deserializer;
  }

  //TODO map href and meta
  /**
   * Will map links and return them.
   *
   * @param linksJsonObject JSONObject from link.
   * @return Links with mapped values.
   */
  public Links mapLinks(JSONObject linksJsonObject) {
    Links links = new Links();
    try {
      links.selfLink = linksJsonObject.getString("self");
    } catch (JSONException e) {
      Logger.debug("JSON link does not contain self");
    }

    try {
      links.related = linksJsonObject.getString("related");
    } catch (JSONException e) {
      Logger.debug("JSON link does not contain related");
    }

    try {
      links.first = linksJsonObject.getString("first");
    } catch (JSONException e) {
      Logger.debug("JSON link does not contain first");
    }

    try {
      links.last = linksJsonObject.getString("last");
    } catch (JSONException e) {
      Logger.debug("JSON link does not contain last");
    }

    try {
      links.prev = linksJsonObject.getString("prev");
    } catch (JSONException e) {
      Logger.debug("JSON link does not contain prev");
    }

    try {
      links.next = linksJsonObject.getString("next");
    } catch (JSONException e) {
      Logger.debug("JSON link does not contain next");
    }

    return links;
  }

  /**
   * Will loop through meta JSONObject and return values as arrayMap.
   *
   * @param jsonObject JSONObject for meta.
   * @return ArrayMap with meta values.
   */
  public ArrayMap<String, Object> jsonObjectToArrayMap(JSONObject jsonObject) {
    ArrayMap<String, Object> metaMap = new ArrayMap<>();

    for(Iterator<String> iter = jsonObject.keys(); iter.hasNext();) {
      String key = iter.next();

      try {
        metaMap.put(key, jsonObject.get(key));
      } catch (JSONException e) {
        Logger.debug("JSON does not contain " + key + ".");
      }
    }

    return metaMap;
  }

  /**
   * Map the Id from json to the object.
   *
   * @param object Object of the class.
   * @param jsonDataObject JSONObject of the dataNode.
   * @return Object with mapped fields.
   * @throws Exception
   */
  public MorpheusResource mapId(MorpheusResource object, JSONObject jsonDataObject) throws Exception {
    try {
      return mDeserializer.setIdField(object, jsonDataObject.get("id"));
    } catch (JSONException e) {
      Logger.debug("JSON data does not contain id.");
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
  public MorpheusResource mapAttributes(MorpheusResource object, JSONObject attributesJsonObject) {

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
          if (attributesJsonObject.get(jsonFieldName).getClass() == JSONArray.class) {
            List<Object> attributeAsList = new ArrayList<>();
            JSONArray attributeJsonArray = attributesJsonObject.getJSONArray(jsonFieldName);
            for (int i = 0; attributeJsonArray.length() > i; i++) {
              attributeAsList.add(attributeJsonArray.get(i));
            }
            mDeserializer.setField(object, field.getName(), attributeAsList);
          } else if (attributesJsonObject.get(jsonFieldName).getClass() == JSONObject.class) {
            ArrayMap<String, Object> dictionary = new ArrayMap<>();
            JSONObject objectForMap = attributesJsonObject.getJSONObject(jsonFieldName);
            mDeserializer.setField(object, field.getName(), jsonObjectToArrayMap(objectForMap));
          } else {
            mDeserializer.setField(object, field.getName(), attributesJsonObject.get(jsonFieldName));
          }
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
  public MorpheusResource mapRelations(MorpheusResource object, JSONObject jsonObject,
                                       List<MorpheusResource> included) throws Exception {
    ArrayMap<String, String> relationshipNames = getRelationshipNames(object.getClass());

    //going through relationship names annotated in Class
    for (String relationship : relationshipNames.keySet()) {
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
        MorpheusResource relationObject = Factory.newObjectFromJSONObject(relationDataObject, null);

        relationObject = matchIncludedToRelation(relationObject, included);

        mDeserializer.setField(object, relationshipNames.get(relationship), relationObject);
      } catch (JSONException e) {
        Logger.debug("JSON relationship does not contain data");
      }

      //map json array of data
      JSONArray relationDataArray = null;
      try {
        relationDataArray = relationJsonObject.getJSONArray("data");
        List<MorpheusResource> relationArray = Factory.newObjectFromJSONArray(relationDataArray, null);

        relationArray = matchIncludedToRelation(relationArray, included);

        mDeserializer.setField(object, relationshipNames.get(relationship), relationArray);
      } catch (JSONException e) {
        Logger.debug("JSON relationship does not contain data");
      }
    }

    return object;
  }


  /**
   * Will check if the relation is included. If true included object will be returned.
   *
   * @param object Relation resources.
   * @param included List of included resources.
   * @return Relation of included resource.
   */
  public MorpheusResource matchIncludedToRelation(MorpheusResource object, List<MorpheusResource> included) {
    for (MorpheusResource resource : included) {
      if (object.getId().equals(resource.getId()) && object.getClass().equals(resource.getClass())) {
        return resource;
      }
    }
    return object;
  }

  /**
   * Loops through relations and calls {@link #matchIncludedToRelation(MorpheusResource, List)}.
   *
   * @param relationResources List of relation resources.
   * @param included List of included resources.
   * @return List of relations and/or included resources.
   */
  public List<MorpheusResource> matchIncludedToRelation(List<MorpheusResource> relationResources, List<MorpheusResource> included) {
    List<MorpheusResource> matchedResources = new ArrayList<>();
    for (MorpheusResource resource : relationResources) {
      matchedResources.add(matchIncludedToRelation(resource, included));
    }
    return matchedResources;
  }

  //helper

  /**
   * Get the annotated relationship names.
   *
   * @param clazz Class for annotation.
   * @return List of relationship names.
   */
  private  ArrayMap<String, String> getRelationshipNames(Class clazz) {
    ArrayMap<String, String> relationNames = new ArrayMap<>();
    for (Field field : clazz.getDeclaredFields()) {
      String fieldName = field.getName();
      for (Annotation annotation : field.getDeclaredAnnotations()) {
        if (annotation.annotationType() == SerializeName.class) {
          SerializeName serializeName = (SerializeName)annotation;
          fieldName = serializeName.jsonName();
        }
        if (annotation.annotationType() == Relationship.class) {
          Relationship relationshipAnnotation = (Relationship)annotation;
          relationNames.put(relationshipAnnotation.relationName(), fieldName);
        }
      }
    }

    return relationNames;
  }
}
