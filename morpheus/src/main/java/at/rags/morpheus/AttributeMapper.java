package at.rags.morpheus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * AttributeMapper is used to map the json:api attribute node to
 * your object fields.
 *
 * You can create your own AttributeMapper and set it via {@link Morpheus#Morpheus(AttributeMapper)}.
 */
public class AttributeMapper {
  private Deserializer mDeserializer;

  public AttributeMapper() {
    mDeserializer = new Deserializer();
  }

  public AttributeMapper(Deserializer deserializer) {
    mDeserializer = deserializer;
  }

  /**
   * Will map the attributes of the JSONAPI attribute object.
   * JSONArrays will get mapped as {@literal List<Object>}.
   * JSONObject will get mapped as {@literal ArrayMap<String, Object>}.
   * Everything else will get mapped without changes.
   *
   * @param jsonApiResource Object extended with {@link Resource} that will get the field set.
   * @param attributesJsonObject {@link JSONObject} with json:api attributes object
   * @param field Field that will be set.
   * @param jsonFieldName Name of the json-field in attributesJsonObject to get data from.
   */
  public void mapAttributeToObject(Resource jsonApiResource, JSONObject attributesJsonObject, Field field, String jsonFieldName) {
    try {
      if (attributesJsonObject.get(jsonFieldName).getClass() == JSONArray.class) {
        List<Object> list = createListFromJSONArray(attributesJsonObject.getJSONArray(jsonFieldName));
        mDeserializer.setField(jsonApiResource, field.getName(), list);
      } else if (attributesJsonObject.get(jsonFieldName).getClass() == JSONObject.class) {
        JSONObject objectForMap = attributesJsonObject.getJSONObject(jsonFieldName);
        mDeserializer.setField(jsonApiResource, field.getName(), createMapFromJSONObject(objectForMap));
      } else {
        mDeserializer.setField(jsonApiResource, field.getName(), attributesJsonObject.get(jsonFieldName));
      }
    } catch (JSONException e) {
      Logger.debug("JSON attributes does not contain " + jsonFieldName);
    }
  }

  /**
   * Will loop through JSONArray and return values as List<Object>.
   *
   * @param jsonArray JSONArray with values.
   * @return List<Object> of JSONArray values.
   */
  private List<Object> createListFromJSONArray(JSONArray jsonArray) {
    List<Object> attributeAsList = new ArrayList<>();
    for (int i = 0; jsonArray.length() > i; i++) {
      try {
        attributeAsList.add(jsonArray.get(i));
      } catch (JSONException e) {
        Logger.debug("JSONArray does not contain Object at index " + i);
      }
    }
    return attributeAsList;
  }

  /**
   * Will loop through JSONObject and return values as arrayMap.
   *
   * @param jsonObject JSONObject for meta.
   * @return HashMap with meta values.
   */
  public HashMap<String, Object> createMapFromJSONObject(JSONObject jsonObject) {
    HashMap<String, Object> metaMap = new HashMap<>();

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

}
