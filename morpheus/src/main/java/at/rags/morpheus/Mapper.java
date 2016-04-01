package at.rags.morpheus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.rags.morpheus.Annotations.Relationship;
import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.Exceptions.NotExtendingResourceException;

/**
 * Mapper will map all different top-level members and will
 * also map the relations.
 *
 * Includes will also mapped to matching relationship members.
 */
public class Mapper {

  private Deserializer mDeserializer;
  private AttributeMapper mAttributeMapper;

  public Mapper() {
    mDeserializer = new Deserializer();
    mAttributeMapper = new AttributeMapper();
  }

  public Mapper(Deserializer deserializer, AttributeMapper attributeMapper) {
    mDeserializer = deserializer;
    mAttributeMapper = attributeMapper;
  }

  //TODO map href and meta (http://jsonapi.org/format/#document-links)
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
   * Map the Id from json to the object.
   *
   * @param object Object of the class.
   * @param jsonDataObject JSONObject of the dataNode.
   * @return Object with mapped fields.
   * @throws NotExtendingResourceException Throws when the object is not extending {@link Resource}
   */
  public Resource mapId(Resource object, JSONObject jsonDataObject) throws NotExtendingResourceException {
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
  public Resource mapAttributes(Resource object, JSONObject attributesJsonObject) {
    if (attributesJsonObject == null) {
      return object;
    }

    for (Field field : object.getClass().getDeclaredFields()) {
      // get the right attribute name
      String jsonFieldName = field.getName();
      boolean isRelation = false;
      for (Annotation annotation : field.getAnnotations()) {
        if (annotation.annotationType() == SerializeName.class) {
          SerializeName serializeName = (SerializeName) annotation;
          jsonFieldName = serializeName.value();
        }
        if (annotation.annotationType() == Relationship.class) {
          isRelation = true;
        }
      }

      if (isRelation) {
        continue;
      }

      mAttributeMapper.mapAttributeToObject(object, attributesJsonObject, field, jsonFieldName);
    }

    return object;
  }

  /**
   * Loops through relation JSON array and maps annotated objects.
   *
   * @param object Real object to map.
   * @param jsonObject JSONObject.
   * @param included List of included resources.
   * @return Real object with relations.
   * @throws Exception when deserializer is not able to create instance.
   */
  public Resource mapRelations(Resource object, JSONObject jsonObject,
                                       List<Resource> included) throws Exception {
    HashMap<String, String> relationshipNames = getRelationshipNames(object.getClass());

    //going through relationship names annotated in Class
    for (String relationship : relationshipNames.keySet()) {
      JSONObject relationJsonObject = null;
      try {
        relationJsonObject = jsonObject.getJSONObject(relationship);
      } catch (JSONException e) {
        Logger.debug("Relationship named " + relationship + "not found in JSON");
        continue;
      }

      //map json object of data
      JSONObject relationDataObject = null;
      try {
        relationDataObject = relationJsonObject.getJSONObject("data");
        Resource relationObject = Factory.newObjectFromJSONObject(relationDataObject, null);

        relationObject = matchIncludedToRelation(relationObject, included);

        mDeserializer.setField(object, relationshipNames.get(relationship), relationObject);
      } catch (JSONException e) {
        Logger.debug("JSON relationship does not contain data");
      }

      //map json array of data
      JSONArray relationDataArray = null;
      try {
        relationDataArray = relationJsonObject.getJSONArray("data");
        List<Resource> relationArray = Factory.newObjectFromJSONArray(relationDataArray, null);

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
  public Resource matchIncludedToRelation(Resource object, List<Resource> included) {
    if (included == null) {
      return object;
    }

    for (Resource resource : included) {
      if (object.getId().equals(resource.getId()) && object.getClass().equals(resource.getClass())) {
        return resource;
      }
    }
    return object;
  }

  /**
   * Loops through relations and calls {@link #matchIncludedToRelation(Resource, List)}.
   *
   * @param relationResources List of relation resources.
   * @param included List of included resources.
   * @return List of relations and/or included resources.
   */
  public List<Resource> matchIncludedToRelation(List<Resource> relationResources, List<Resource> included) {
    List<Resource> matchedResources = new ArrayList<>();
    for (Resource resource : relationResources) {
      matchedResources.add(matchIncludedToRelation(resource, included));
    }
    return matchedResources;
  }

  public List<Error> mapErrors(JSONArray errorArray) {
    List<Error> errors = new ArrayList<>();

    for (int i = 0; errorArray.length() > i; i++) {
      JSONObject errorJsonObject;
      try {
        errorJsonObject = errorArray.getJSONObject(i);
      } catch (JSONException e) {
        Logger.debug("No index " + i + " in error json array");
        continue;
      }
      Error error = new Error();

      try {
        error.setId(errorJsonObject.getString("id"));
      } catch (JSONException e) {
        Logger.debug("JSON object does not contain id");
      }

      try {
        error.setStatus(errorJsonObject.getString("status"));
      } catch (JSONException e) {
        Logger.debug("JSON object does not contain status");
      }

      try {
        error.setCode(errorJsonObject.getString("code"));
      } catch (JSONException e) {
        Logger.debug("JSON object does not contain code");
      }

      try {
        error.setTitle(errorJsonObject.getString("title"));
      } catch (JSONException e) {
        Logger.debug("JSON object does not contain title");
      }

      try {
        error.setDetail(errorJsonObject.getString("detail"));
      } catch (JSONException e) {
        Logger.debug("JSON object does not contain detail");
      }

      JSONObject sourceJsonObject = null;
      try {
        sourceJsonObject = errorJsonObject.getJSONObject("source");
      }
      catch (JSONException e) {
        Logger.debug("JSON object does not contain source");
      }

      if (sourceJsonObject != null) {
        Source source = new Source();
        try {
          source.setParameter(sourceJsonObject.getString("parameter"));
        } catch (JSONException e) {
          Logger.debug("JSON object does not contain parameter");
        }
        try {
          source.setPointer(sourceJsonObject.getString("pointer"));
        } catch (JSONException e) {
          Logger.debug("JSON object does not contain pointer");
        }
        error.setSource(source);
      }

      try {
        JSONObject linksJsonObject = errorJsonObject.getJSONObject("links");
        ErrorLinks links = new ErrorLinks();
        links.setAbout(linksJsonObject.getString("about"));
        error.setLinks(links);
      }
      catch (JSONException e) {
        Logger.debug("JSON object does not contain links or about");
      }

      try {
        error.setMeta(mAttributeMapper.createMapFromJSONObject(errorJsonObject.getJSONObject("meta")));
      } catch (JSONException e) {
        Logger.debug("JSON object does not contain JSONObject meta");
      }

      errors.add(error);
    }

    return errors;
  }

  //helper

  /**
   * Get the annotated relationship names.
   *
   * @param clazz Class for annotation.
   * @return List of relationship names.
   */
  private HashMap<String, String> getRelationshipNames(Class clazz) {
    HashMap<String, String> relationNames = new HashMap<>();
    for (Field field : clazz.getDeclaredFields()) {
      String fieldName = field.getName();
      for (Annotation annotation : field.getDeclaredAnnotations()) {
        if (annotation.annotationType() == SerializeName.class) {
          SerializeName serializeName = (SerializeName)annotation;
          fieldName = serializeName.value();
        }
        if (annotation.annotationType() == Relationship.class) {
          Relationship relationshipAnnotation = (Relationship)annotation;
          relationNames.put(relationshipAnnotation.value(), fieldName);
        }
      }
    }

    return relationNames;
  }

  // getter

  public Deserializer getDeserializer() {
    return mDeserializer;
  }

  public AttributeMapper getAttributeMapper() {
    return mAttributeMapper;
  }
}
