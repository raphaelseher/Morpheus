package at.rags.morpheus;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by raphaelseher on 10/03/16.
 */
public class AttributeMapper {

  private Deserializer mDeserializer;

  public AttributeMapper() {
    mDeserializer = new Deserializer();
  }

  public AttributeMapper(Deserializer deserializer) {
    mDeserializer = deserializer;
  }

  public void mapAttributeToObject(MorpheusResource object, JSONObject attributesJsonObject, Field field, String jsonFieldName) {
    try {
      if (attributesJsonObject.get(jsonFieldName).getClass() == JSONArray.class) {
        List<Object> list = mapArray(object, attributesJsonObject.getJSONArray(jsonFieldName));
        mDeserializer.setField(object, field.getName(), list);
      } else if (attributesJsonObject.get(jsonFieldName).getClass() == JSONObject.class) {
        JSONObject objectForMap = attributesJsonObject.getJSONObject(jsonFieldName);
        mDeserializer.setField(object, field.getName(), jsonObjectToArrayMap(objectForMap));
      } else {
        mDeserializer.setField(object, field.getName(), attributesJsonObject.get(jsonFieldName));
      }
    } catch (JSONException e) {
      Logger.debug("JSON attributes does not contain " + jsonFieldName);
    }
  }

  private List<Object> mapArray(MorpheusResource object, JSONArray jsonArray) {
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

}
