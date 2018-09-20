package at.rags.morpheus;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * AttributeMapper is used to map the json:api attribute node to
 * your object fields.
 * <p>
 * You can create your own AttributeMapper and set it via {@link Morpheus#Morpheus(at.rags.morpheus.AttributeMapper)}.
 */
public class AttributeMapper {
    private Deserializer deserializer;
    private Gson gson;

    public AttributeMapper() {
        deserializer = new Deserializer();
        gson = new Gson();
    }

    public AttributeMapper(Deserializer deserializer, Gson gson) {
        this.deserializer = deserializer;
        this.gson = gson;
    }

    /**
     * Will map the attributes of the JSONAPI attribute object.
     * JSONArrays will get mapped as {@literal List<Object>}.
     * JSONObject will get mapped as {@literal ArrayMap<String, Object>}.
     * Everything else will get mapped without changes.
     *
     * @param jsonApiResource      Object extended with {@link Resource} that will get the field set.
     * @param objClass
     * @param attributesJsonObject {@link JSONObject} with json:api attributes object
     * @param field                Field that will be set.
     * @param jsonFieldName        Name of the json-field in attributesJsonObject to get data from.
     */
    public void mapAttributeToObject(Resource jsonApiResource, Class<? extends Resource> objClass, JSONObject attributesJsonObject,
                                     Field field, String jsonFieldName) {

        Object object = null;
        try {
            object = attributesJsonObject.get(jsonFieldName);
        } catch (JSONException e) {
            Logger.debug(attributesJsonObject.toString() + " does not contain " + jsonFieldName);
            return;
        }
        if (objClass == null) {
            objClass = jsonApiResource.getClass();
        }

        if (object instanceof JSONArray) {
            if (field.getType().isAssignableFrom(List.class)) {
                List<Object> list = null;
                try {
                    list = createListFromJSONArray(attributesJsonObject.getJSONArray(jsonFieldName), field);
                } catch (JSONException e) {
                    Logger.debug(jsonFieldName + " is not an valid JSONArray.");
                }

                deserializer.setField(jsonApiResource, objClass, field.getName(), list);
            } else {
                Object obj = gson.fromJson(object.toString(), field.getType());
                deserializer.setField(jsonApiResource, objClass, field.getName(), obj);
            }
        } else if (object.getClass() == JSONObject.class) {
            Object obj = gson.fromJson(object.toString(), field.getType());
            deserializer.setField(jsonApiResource, objClass, field.getName(), obj);
        } else if (JSONObject.NULL != object) {
            JsonReader reader = gson.newJsonReader(new StringReader(object.toString()));
            if (field.getType().isEnum()) {
                reader.setLenient(true);
            }
            try {
                object = gson.getAdapter(field.getType()).read(reader);
            } catch (IOException e) {
                Logger.debug(jsonFieldName + " failed to read.");
            }
            deserializer.setField(jsonApiResource, objClass, field.getName(), object);
        }

    }

    /**
     * Will loop through JSONArray and return values as List<Object>.
     *
     * @param jsonArray JSONArray with values.
     * @return List<Object> of JSONArray values.
     */
    private List<Object> createListFromJSONArray(JSONArray jsonArray, Field field) {
        Type genericFieldType = field.getGenericType();
        List<Object> objectArrayList = new ArrayList<>();

        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for (Type fieldArgType : fieldArgTypes) {
                final Class fieldArgClass = (Class) fieldArgType;

                for (int i = 0; jsonArray.length() > i; i++) {
                    Object obj = null;
                    Object jsonObject = null;

                    try {
                        jsonObject = jsonArray.get(i);
                    } catch (JSONException e) {
                        Logger.debug("JSONArray does not contain index " + i + ".");
                        continue;
                    }

                    // if this is a String, it wont use gson because it can throw a malformed json exception
                    // that case happens if there is a String with ":" in it.
                    if (fieldArgClass == String.class) {
                        obj = jsonObject.toString();
                    } else {
                        try {
                            obj = gson.fromJson(jsonArray.get(i).toString(), fieldArgClass);
                        } catch (JSONException e) {
                            Logger.debug("JSONArray does not contain index " + i + ".");
                        }
                    }

                    objectArrayList.add(obj);
                }
            }
        }

        return objectArrayList;
    }

    /**
     * Will loop through JSONObject and return values as map.
     *
     * @param jsonObject JSONObject for meta.
     * @return HashMap with meta values.
     */
    public HashMap<String, Object> createMapFromJSONObject(JSONObject jsonObject) {
        HashMap<String, Object> metaMap = new HashMap<>();
        if (jsonObject == null) return metaMap;

        for (Iterator<String> iter = jsonObject.keys(); iter.hasNext(); ) {
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
