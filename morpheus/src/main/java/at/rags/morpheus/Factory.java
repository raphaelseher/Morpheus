package at.rags.morpheus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.rags.morpheus.exceptions.NotExtendingResourceException;
import at.rags.morpheus.exceptions.ResourceCreationException;

/**
 * Factory to create and map {@link at.rags.morpheus.Resource}.
 */
class Factory {

    private static Mapper mapper = new Mapper();
    private static Deserializer deserializer = new Deserializer();

    /**
     * Deserializes a json object of data to the registered class.
     *
     * @param dataObject JSONObject from data
     * @param included   {@literal List<Resource>} from includes to automatic match them.
     * @return Deserialized Object.
     * @throws ResourceCreationException     when deserializer is not able to create instance.
     * @throws NotExtendingResourceException when deserializer is not able to create instance.
     */
    static Resource newObjectFromJSONObject(JSONObject dataObject, List<at.rags.morpheus.Resource> included)
        throws ResourceCreationException, NotExtendingResourceException {
        at.rags.morpheus.Resource realObject = null;

        if (dataObject == null || dataObject.isNull("type")) return null;
        String type = dataObject.optString("type");
        try {
            realObject = deserializer.createObjectFromString(type);
        } catch (IllegalAccessException e) {
            throw new ResourceCreationException(e);
        } catch (InstantiationException e) {
            throw new ResourceCreationException(e);
        } catch (NotExtendingResourceException e) {
            throw e;
        }

        if (realObject == null) return null;
        realObject = mapper.mapId(realObject, dataObject);
        realObject = mapper.mapType(realObject, dataObject);
        try {
            realObject = mapper.mapAttributes(realObject, dataObject.getJSONObject("attributes"));
        } catch (JSONException e) {
            Logger.debug("JSON does not contain attributes");
        }

        try {
            realObject = mapper.mapRelations(realObject, dataObject.getJSONObject("relationships"), included);
        } catch (JSONException e) {
            Logger.debug("JSON data does not contain relationships");
        }

        try {
            assert realObject != null;
            realObject.setMeta(dataObject.getJSONObject("meta"));
        } catch (JSONException e) {
            Logger.debug("JSON data does not contain meta");
        }

        try {
            realObject.setLinks(mapper.mapLinks(dataObject.getJSONObject("links")));
        } catch (JSONException e) {
            Logger.debug("JSON data does not contain links");
        }

        return realObject;
    }

    /**
     * Loops through data objects and deserializes them.
     *
     * @param dataArray JSONArray of the data node.
     * @param included  {@literal List<Resource>} from includes to automatic match them.
     * @return List of deserialized objects.
     * @throws ResourceCreationException     when deserializer is not able to create instance.
     * @throws NotExtendingResourceException when deserializer is not able to create instance.
     */
    static List<Resource> newObjectFromJSONArray(JSONArray dataArray, List<Resource> included)
        throws ResourceCreationException, NotExtendingResourceException {
        ArrayList<Resource> objects = new ArrayList<>();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = null;

            try {
                jsonObject = dataArray.getJSONObject(i);
            } catch (JSONException e) {
                Logger.debug("Was not able to get dataArray[" + i + "] as JSONObject.");
            }
            Resource resource = newObjectFromJSONObject(jsonObject, included);
            if (resource != null) objects.add(resource);
        }
        return objects;
    }

    // helper

    static void setDeserializer(Deserializer deserializer) {
        at.rags.morpheus.Factory.deserializer = deserializer;
    }

    static void setMapper(Mapper mapper) {
        at.rags.morpheus.Factory.mapper = mapper;
    }
}
