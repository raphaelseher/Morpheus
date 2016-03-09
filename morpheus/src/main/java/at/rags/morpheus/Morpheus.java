package at.rags.morpheus;

import android.annotation.TargetApi;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Work in progress.
 */
public class Morpheus {

  private Mapper mapper;

  @TargetApi(Build.VERSION_CODES.KITKAT)
  public Morpheus() {
    mapper = new Mapper();
  }

  public JSONAPIObject jsonToObject(String jsonString) throws Exception {
    JSONAPIObject jsonapiObject = new JSONAPIObject();

    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(jsonString);
    } catch (Exception e) {
      throw new Exception("Invalid JSON String.");
    }

    //included
    JSONArray includedArray = null;
    try {
      includedArray = jsonObject.getJSONArray("included");
      jsonapiObject.setIncluded(Factory.newObjectFromJSONArray(includedArray, null));
    } catch (JSONException e) {
      Logger.debug("JSON does not contain included");
    }

    //data array
    JSONArray dataArray = null;
    try {
      dataArray = jsonObject.getJSONArray("data");
      jsonapiObject.setResources(Factory.newObjectFromJSONArray(dataArray, jsonapiObject.getIncluded()));
    } catch (JSONException e) {
      Logger.debug("JSON does not contain data array");
    }

    //data object
    JSONObject dataObject = null;
    try {
      dataObject = jsonObject.getJSONObject("data");
      jsonapiObject.setResource(Factory.newObjectFromJSONObject(dataObject, jsonapiObject.getIncluded()));
    } catch (JSONException e) {
      Logger.debug("JSON does not contain data object");
    }

    //link object
    JSONObject linkObject = null;
    try {
      linkObject = jsonObject.getJSONObject("links");
      jsonapiObject.setLinks(mapper.mapLinks(linkObject));
    } catch (JSONException e) {
      Logger.debug("JSON does not contain links object");
    }

    //meta object
    JSONObject metaObject = null;
    try {
      metaObject = jsonObject.getJSONObject("meta");
      jsonapiObject.setMeta(mapper.jsonObjectToArrayMap(metaObject));
    } catch (JSONException e) {
      Logger.debug("JSON does not contain meta object");
    }

    //TODO errors

    return jsonapiObject;
  }
}
