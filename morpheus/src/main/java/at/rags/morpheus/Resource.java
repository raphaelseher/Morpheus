package at.rags.morpheus;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Extend this resource to your custom Object you want to map.
 * You can set custom json object names and relationships via the provided annotations.
 * <pre>
 * {@code
 * public class Article extends Resource { ... }
 * }</pre>
 *
 * @see com.google.gson.annotations.SerializedName
 * @see at.rags.morpheus.annotations.Relationship
 */
public class Resource implements Serializable {

    private String id;
    private String type;
    private at.rags.morpheus.Links links;
    private String meta;
    private Map<String, String> relationshipMetas;

    public Resource() {
    }

    public JSONObject getMeta() {
        if (meta == null) return null;
        try {
            return new JSONObject(meta);
        } catch (JSONException e) {
            return null;
        }
    }

    public void setMeta(JSONObject meta) {
        if (meta == null) return;
        this.meta = meta.toString();
    }

    public Map<String, JSONObject> getRelationshipMetas() {
        if (relationshipMetas == null) return null;
        Map<String, JSONObject> metas = new HashMap<>();
        for (Map.Entry<String, String> meta : relationshipMetas.entrySet()) {
            if (meta.getValue() == null) continue;
            try {
                metas.put(meta.getKey(), new JSONObject(meta.getValue()));
            } catch (JSONException e) {
            }
        }
        return metas;
    }

    public void setRelationshipMeta(String key, JSONObject meta) {
        if (relationshipMetas == null) {
            relationshipMetas = new HashMap<>();
        }
        relationshipMetas.put(key, meta.toString());
    }

    public at.rags.morpheus.Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class ResourceSerializer<T> implements JsonSerializer<T> {

        @Override
        public JsonObject serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            if (src instanceof Resource) {
                Resource resource = (Resource) src;
                jsonObject.addProperty("id", resource.getId());
                jsonObject.addProperty("type", resource.getType());
            }
            Class srcClass = src.getClass();
//            while (srcClass != null && srcClass != Resource.class) {
            Field[] fields = srcClass.getDeclaredFields();
//            Log.d("JSONApi", "class:" + src.getClass());
            for (Field field : fields) {
                SerializedName serializedName = field.getAnnotation(SerializedName.class);
                if (serializedName == null) {
                    continue;
                }
//                Log.d("JSONApi", "SerializedName:" + serializedName.value());
//                Log.d("JSONApi", "Type:" + field.getType());
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    if (int.class == field.getType()) {
                        jsonObject.addProperty(serializedName.value(), field.getInt(src));
                    } else if (long.class.equals(field.getType())) {
                        jsonObject.addProperty(serializedName.value(), field.getLong(src));
                    } else if (float.class.isAssignableFrom(field.getType())) {
                        jsonObject.addProperty(serializedName.value(), field.getFloat(src));
                    } else if (double.class.isAssignableFrom(field.getType())) {
                        jsonObject.addProperty(serializedName.value(), field.getDouble(src));
                    } else if (boolean.class.isAssignableFrom(field.getType())) {
                        jsonObject.addProperty(serializedName.value(), field.getBoolean(src));
                    } else if (String.class.equals(field.getType())) {
                        jsonObject.addProperty(serializedName.value(), field.get(src) == null ? null : "" + field.get(src));
                    } else {
                        jsonObject.add(serializedName.value(), context.serialize(field.get(src)));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } finally {
                    field.setAccessible(accessible);
                }
//                    srcClass = srcClass.getSuperclass();
//                }
            }
            return jsonObject;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (!getClass().isInstance(obj) && !obj.getClass().isInstance(this)))
            return false;
        Resource that = (Resource) obj;
        return this.id.equals(that.id);
    }
}

