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
public class MorpheusMapper {

  public MorpheusMapper() {
  }

  /**
   * Deserializes a json object of data to the registered class.
   *
   * @param dataObject JSONObject from data
   * @return Deserialized Object.
   */
  public MorpheusResource mapDataObject(JSONObject dataObject, List<MorpheusResource> included) {
    MorpheusResource realObject = null;

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
      realObject = mapRelations(realObject, dataObject.getJSONObject("relationships"), included);
    } catch (Exception e) {
      Logger.debug("JSON data does not contain relationships");
    }

    try {
      realObject.setLinks(mapLinks(dataObject.getJSONObject("links")));
    } catch (JSONException e) {
      Logger.debug("JSON data does not contain links");
    }

    //TODO meta

    return realObject;
  }

  /**
   * Loops through data objects and deserializes them.
   *
   * @param dataArray JSONArray of the data node.
   * @return List of deserialized objects.
   */
  public List<MorpheusResource> mapDataArray(JSONArray dataArray, List<MorpheusResource> included) {
    ArrayList<MorpheusResource> objects = new ArrayList<>();

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject jsonObject = null;

      try {
        jsonObject = dataArray.getJSONObject(i);
      } catch (JSONException e) {
        Logger.debug("Was not able to get dataArray["+i+"] as JSONObject.");
      }
      objects.add(mapDataObject(jsonObject, included));
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
  public MorpheusResource mapId(MorpheusResource object, JSONObject jsonDataObject) {
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
          MorpheusDeserializer.setField(object, field.getName(), attributesJsonObject.get(jsonFieldName));
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
        MorpheusResource relationObject = mapDataObject(relationDataObject, null);

        relationObject = matchIncludedToRelation(relationObject, included);

        MorpheusDeserializer.setField(object, relationship, relationObject);
      } catch (JSONException e) {
        Logger.debug("JSON relationship does not contain data");
      }

      //map json array of data
      JSONArray relationDataArray = null;
      try {
        relationDataArray = relationJsonObject.getJSONArray("data");
        List<MorpheusResource> relationArray = mapDataArray(relationDataArray, null);

        relationArray = matchIncludedToRelation(relationArray, included);

        MorpheusDeserializer.setField(object, relationship, relationArray);
      } catch (JSONException e) {
        Logger.debug("JSON relationship does not contain data");
      }
    }

    return object;
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
   * @param metaJsonObject JSONObject for meta.
   * @return ArrayMap with meta values.
   */
  public ArrayMap<String, Object> mapMeta(JSONObject metaJsonObject) {
    ArrayMap<String, Object> metaMap = new ArrayMap<>();

    for(Iterator<String> iter = metaJsonObject.keys();iter.hasNext();) {
      String key = iter.next();

      try {
        metaMap.put(key, metaJsonObject.get(key));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return metaMap;
  }


  /**
   * Will check if the relation is included. If true included object will be returned.
   *
   * @param object Relation resources.
   * @param included List of included resources.
   * @return Relation of included resource.
   */
  private MorpheusResource matchIncludedToRelation(MorpheusResource object, List<MorpheusResource> included) {
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
  private List<MorpheusResource> matchIncludedToRelation(List<MorpheusResource> relationResources, List<MorpheusResource> included) {
    List<MorpheusResource> matchedResources = new ArrayList<>();
    for (MorpheusResource resource : relationResources) {
      matchedResources.add(matchIncludedToRelation(resource, included));
    }
    return matchedResources;
  }

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
