package at.rags.morpheus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to create and map {@link Resource}.
 */
public class Factory {

  private static Mapper mapper = new Mapper();
  private static Deserializer deserializer = new Deserializer();

  /**
   * Deserializes a json object of data to the registered class.
   *
   * @param dataObject JSONObject from data
   * @return Deserialized Object.
   */
  public static Resource newObjectFromJSONObject(JSONObject dataObject, List<Resource> included) {
    Resource realObject = null;

    try {
      realObject = deserializer.createObjectFromString(getTypeFromJson(dataObject));
    } catch (Exception e) {
      Logger.debug(e.getMessage());
    }

    try {
      realObject = mapper.mapId(realObject, dataObject);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      realObject = mapper.mapAttributes(realObject, dataObject.getJSONObject("attributes"));
    } catch (Exception e) {
      e.printStackTrace();
      Logger.debug("JSON data does not contain attributes");
    }

    try {
      realObject = mapper.mapRelations(realObject, dataObject.getJSONObject("relationships"), included);
    } catch (Exception e) {
      Logger.debug("JSON data does not contain relationships");
    }

    try {
      assert realObject != null;
      realObject.setLinks(mapper.mapLinks(dataObject.getJSONObject("links")));
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
  public static List<Resource> newObjectFromJSONArray(JSONArray dataArray, List<Resource> included) {
    ArrayList<Resource> objects = new ArrayList<>();

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject jsonObject = null;

      try {
        jsonObject = dataArray.getJSONObject(i);
      } catch (JSONException e) {
        Logger.debug("Was not able to get dataArray["+i+"] as JSONObject.");
      }
      objects.add(newObjectFromJSONObject(jsonObject, included));
    }

    return objects;
  }

  // helper

  /**
   * Get the type of the data message.
   *
   * @param object JSONObject.
   * @return Name of the json type.
   */
  public static String getTypeFromJson(JSONObject object) {
    String type = null;
    try {
      type = object.getString("type");
    } catch (JSONException e) {
      Logger.debug("JSON data does not contain type");
    }
    return type;
  }

  public static void setDeserializer(Deserializer deserializer) {
    Factory.deserializer = deserializer;
  }

  public static void setMapper(Mapper mapper) {
    Factory.mapper = mapper;
  }
}
