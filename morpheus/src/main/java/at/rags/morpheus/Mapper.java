package at.rags.morpheus;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.rags.morpheus.annotations.Relationship;
import at.rags.morpheus.exceptions.NotExtendingResourceException;
import at.rags.morpheus.exceptions.ResourceCreationException;

/**
 * Mapper will map all different top-level members and will
 * also map the relations.
 * <p>
 * Includes will also mapped to matching relationship members.
 */
class Mapper {

    private Deserializer deserializer;
    private Serializer serializer;
    private AttributeMapper attributeMapper;

    Mapper() {
        deserializer = new Deserializer();
        serializer = new Serializer();
        attributeMapper = new AttributeMapper();
    }

    Mapper(Deserializer deserializer, Serializer serializer, AttributeMapper attributeMapper) {
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.attributeMapper = attributeMapper;
    }

    //TODO map href and meta (http://jsonapi.org/format/#document-links)

    /**
     * Will map links and return them.
     *
     * @param linksJsonObject JSONObject from link.
     * @return Links with mapped values.
     */
    at.rags.morpheus.Links mapLinks(JSONObject linksJsonObject) {
        at.rags.morpheus.Links links = new at.rags.morpheus.Links();
        try {
            links.setSelfLink(linksJsonObject.getString("self"));
        } catch (JSONException e) {
            Logger.debug("JSON link does not contain self");
        }

        try {
            links.setRelated(linksJsonObject.getString("related"));
        } catch (JSONException e) {
            Logger.debug("JSON link does not contain related");
        }

        try {
            links.setFirst(linksJsonObject.getString("first"));
        } catch (JSONException e) {
            Logger.debug("JSON link does not contain first");
        }

        try {
            links.setLast(linksJsonObject.getString("last"));
        } catch (JSONException e) {
            Logger.debug("JSON link does not contain last");
        }

        try {
            links.setPrev(linksJsonObject.getString("prev"));
        } catch (JSONException e) {
            Logger.debug("JSON link does not contain prev");
        }

        try {
            links.setNext(linksJsonObject.getString("next"));
        } catch (JSONException e) {
            Logger.debug("JSON link does not contain next");
        }

        return links;
    }

    /**
     * Map the Id from json to the object.
     *
     * @param object         Object of the class.
     * @param jsonDataObject JSONObject of the dataNode.
     * @return Object with mapped fields.
     * @throws NotExtendingResourceException Throws when the object is not extending {@link Resource}
     */
    Resource mapId(Resource object, JSONObject jsonDataObject) throws NotExtendingResourceException {
        try {
            return deserializer.setIdField(object, jsonDataObject.get("id"));
        } catch (JSONException e) {
            Logger.debug("JSON data does not contain id.");
        }

        return object;
    }

    /**
     * Map the Type from json to the object.
     *
     * @param object         Object of the class.
     * @param jsonDataObject JSONObject of the dataNode.
     * @return Object with mapped fields.
     */
    Resource mapType(Resource object, JSONObject jsonDataObject) {
        try {
            return deserializer.setTypeField(object, jsonDataObject.getString("type"));
        } catch (JSONException e) {
            Logger.debug("JSON data does not contain type.");
        }

        return object;
    }

    /**
     * Maps the attributes of json to the object.
     *
     * @param object               Object of the class.
     * @param attributesJsonObject Attributes object inside the data node.
     * @return Object with mapped fields.
     */
    Resource mapAttributes(Resource object, JSONObject attributesJsonObject) {
        if (attributesJsonObject == null) {
            return object;
        }

        Class objClass = object.getClass();
        Class superClass;
        while(true) {
            superClass = objClass.getSuperclass();
            for (Field field : objClass.getDeclaredFields()) {
                // get the right attribute name
                String jsonFieldName = field.getName();
                boolean isRelation = false;
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation.annotationType() == SerializedName.class) {
                        SerializedName serializeName = (SerializedName) annotation;
                        jsonFieldName = serializeName.value();
                    }
                    if (annotation.annotationType() == Relationship.class) {
                        isRelation = true;
                    }
                }

                if (!Modifier.isStatic(field.getModifiers()) && !isRelation) {
                    attributeMapper.mapAttributeToObject(object, objClass, attributesJsonObject, field, jsonFieldName);
                }
            }

            if (superClass == Resource.class || superClass == Object.class) {
                break;
            }
            objClass = superClass;
        }

        return object;
    }

    /**
     * Loops through relation JSON array and maps annotated objects.
     *
     * @param object     Real object to map.
     * @param jsonObject JSONObject.
     * @param included   List of included resources.
     * @return Real object with relations.
     * @throws NotExtendingResourceException when deserializer is not able to create instance.
     * @throws ResourceCreationException when deserializer is not able to create instance.
     */
    Resource mapRelations(Resource object, JSONObject jsonObject, List<Resource> included)
        throws NotExtendingResourceException, ResourceCreationException {
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

                if (relationObject != null) {
                    relationObject = matchIncludedToRelation(relationObject, included);
                }

                deserializer.setField(object, relationshipNames.get(relationship), relationObject);
            } catch (JSONException e) {
                Logger.debug("JSON relationship does not contain data");
            }

            //map json array of data
            JSONArray relationDataArray = null;
            try {
                relationDataArray = relationJsonObject.getJSONArray("data");
                List<Resource> relationArray = Factory.newObjectFromJSONArray(relationDataArray, null);

                relationArray = matchIncludedToRelation(relationArray, included);

                deserializer.setField(object, relationshipNames.get(relationship), relationArray);
            } catch (JSONException e) {
                Logger.debug("JSON relationship does not contain data");
            }
        }

        return object;
    }


    /**
     * Will check if the relation is included. If true included object will be returned.
     *
     * @param object   Relation resources.
     * @param included List of included resources.
     * @return Relation of included resource.
     */
    Resource matchIncludedToRelation(Resource object, List<Resource> included) {
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
     * @param included          List of included resources.
     * @return List of relations and/or included resources.
     */
    private List<Resource> matchIncludedToRelation(List<Resource> relationResources, List<Resource> included) {
        List<Resource> matchedResources = new ArrayList<>();
        for (Resource resource : relationResources) {
            matchedResources.add(matchIncludedToRelation(resource, included));
        }
        return matchedResources;
    }

    List<Error> mapErrors(JSONArray errorArray) {
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
            } catch (JSONException e) {
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
            } catch (JSONException e) {
                Logger.debug("JSON object does not contain links or about");
            }

            try {
                error.setMeta(attributeMapper.createMapFromJSONObject(errorJsonObject.getJSONObject("meta")));
            } catch (JSONException e) {
                Logger.debug("JSON object does not contain JSONObject meta");
            }

            errors.add(error);
        }

        return errors;
    }

    /**
     * Create data representation from resources.
     * This will return the representation of the resources as list of maps. Every item contains
     * a map with the resource's id, type and relationships (if any). Attributes are only included
     * when 'includeAttributes' is true.
     *
     * @param resources         List of resources.
     * @param includeAttributes Add attributes map to representation.
     * @return ArrayList of Hashmaps<String, Object>.
     */
    ArrayList<HashMap<String, Object>> createData(List<Resource> resources, boolean includeAttributes) {
        String resourceName = null;
        resourceName = nameForResourceClass(resources.get(0).getClass());
        if (resourceName == null) return null;

        ArrayList<HashMap<String, Object>> dataArray = new ArrayList<>();

        for (Resource resource : resources) {
            HashMap<String, Object> attributes = serializer.getFieldsAsDictionary(resource);

            HashMap<String, Object> resourceRepresentation = new HashMap<>();
            resourceRepresentation.put("type", resourceName);
            resourceRepresentation.put("id", resource.getId());
            if (includeAttributes) {
                resourceRepresentation.put("attributes", attributes);
            }

            HashMap<String, Object> relationships = createRelationships(resource);
            if (relationships != null) {
                resourceRepresentation.put("relationships", relationships);
            }

            dataArray.add(resourceRepresentation);
        }

        return dataArray;
    }

    /**
     * Create data represenation from resource.
     * This will return the repersentation of the resource. The map contains the id, type and
     * relationships (if any). Attributes are only included when 'includeAttributes' is true.
     *
     * @param resource          Resource to create data.
     * @param includeAttributes Add attributes map to representation.
     * @return Hashmaps<String, Object>.
     */
    HashMap<String, Object> createData(Resource resource, boolean includeAttributes) {
        String resourceName = null;
        resourceName = nameForResourceClass(resource.getClass());
        if (resourceName == null) return null;

        HashMap<String, Object> resourceRepresentation = new HashMap<>();
        resourceRepresentation.put("type", resourceName);
        resourceRepresentation.put("id", resource.getId());
        if (includeAttributes) {
            HashMap<String, Object> attributes = serializer.getFieldsAsDictionary(resource);
            if (attributes != null) {
                resourceRepresentation.put("attributes", attributes);
            }
        }

        HashMap<String, Object> relationships = createRelationships(resource);
        if (relationships != null) {
            resourceRepresentation.put("relationships", relationships);
        }

        if (resource.getLinks() != null) {
            resourceRepresentation.put("links",
                createLinks(resource));
        }

        return resourceRepresentation;
    }

    /**
     * Creates the relationships represenation from an resource.
     * Will go through the relationships of a resource and return them as a map.
     * The keys of the returned map will be the resource type of the relationship and the value a map
     * of the data or a list of maps containing data of multiple relations.
     *
     * @param resource Resource to create relationships from.
     * @return HashMap of related resource names with their data.
     */
    HashMap<String, Object> createRelationships(Resource resource) {
        HashMap<String, Object> relations = serializer.getRelationships(resource);
        HashMap<String, Object> relationships = new HashMap<>();

        for (String relationshipName : relations.keySet()) {
            Object relationObject = relations.get(relationshipName);

            if (relationObject instanceof Resource) {
                if (resource.getNullableRelationships().contains(relationshipName)) {
                    HashMap<String, Object> dataObject = new HashMap<>();
                    dataObject.put("data", null);
                    relationships.put(relationshipName, dataObject);
                    continue;
                }

                HashMap<String, Object> data = createData((Resource) relationObject, false);
                if (data != null) {
                    HashMap<String, Object> dataObject = new HashMap<>();
                    dataObject.put("data", data);
                    relationships.put(relationshipName, dataObject);
                }
            }

            if (relationObject instanceof ArrayList) {
                if (resource.getNullableRelationships().contains(relationshipName)) {
                    HashMap<String, Object> dataObject = new HashMap<>();
                    dataObject.put("data", new ArrayList<Object>());
                    relationships.put(relationshipName, dataObject);
                    continue;
                }

                ArrayList dataArray = createData((List) relationObject, false);
                if (dataArray != null) {
                    HashMap<String, Object> dataObject = new HashMap<>();
                    dataObject.put("data", dataArray);
                    relationships.put(relationshipName, dataObject);
                }
            }
        }

        if (relationships.isEmpty()) {
            relationships = null;
        }

        return relationships;
    }

    /**
     * Returns a resources links as a map.
     *
     * @param resource Resource to get links from.
     * @return Map of the links.
     */
    HashMap<String, Object> createLinks(Resource resource) {
        HashMap<String, Object> links = null;

        Links resourceLinks = resource.getLinks();
        if (resourceLinks != null) {
            links = new HashMap<>();
            if (resourceLinks.getSelfLink() != null) {
                links.put("self", resourceLinks.getSelfLink());
            }
            if (resourceLinks.getRelated() != null) {
                links.put("related", resourceLinks.getRelated());
            }
            if (resourceLinks.getFirst() != null) {
                links.put("first", resourceLinks.getFirst());
            }
            if (resourceLinks.getLast() != null) {
                links.put("last", resourceLinks.getLast());
            }
            if (resourceLinks.getPrev() != null) {
                links.put("prev", resourceLinks.getPrev());
            }
            if (resourceLinks.getNext() != null) {
                links.put("next", resourceLinks.getNext());
            }
            if (resourceLinks.getAbout() != null) {
                links.put("about", resourceLinks.getAbout());
            }
        }

        return links;
    }

    /**
     * Create the included as list of maps.
     *
     * @param resource Resource with relations.
     * @return List of maps.
     */
    @SuppressWarnings("unchecked")
    ArrayList<HashMap<String, Object>> createIncluded(Resource resource) {
        HashMap<String, Object> relations = serializer.getRelationships(resource);
        ArrayList<HashMap<String, Object>> includes = new ArrayList<>();

        for (String relationshipName : relations.keySet()) {
            Object relationObject = relations.get(relationshipName);
            if (relationObject instanceof Resource) {
                HashMap<String, Object> data = createData((Resource) relationObject, true);
                if (data != null) {
                    includes.add(data);
                }
            }

            if (relationObject instanceof ArrayList) {
                ArrayList dataArray = createData((List) relationObject, true);
                if (dataArray != null) {
                    includes.addAll(dataArray);
                }
            }
        }

        return includes;
    }


    // helper

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
                if (annotation.annotationType() == SerializedName.class) {
                    SerializedName serializeName = (SerializedName) annotation;
                    fieldName = serializeName.value();
                }
                if (annotation.annotationType() == Relationship.class) {
                    Relationship relationshipAnnotation = (Relationship) annotation;
                    relationNames.put(relationshipAnnotation.value(), fieldName);
                }
            }
        }

        return relationNames;
    }

    private String nameForResourceClass(Class clazz) {
        for (String key : Deserializer.getRegisteredClasses().keySet()) {
            if (Deserializer.getRegisteredClasses().get(key) == clazz) {
                return key;
            }
        }
        Logger.debug("Class " + clazz.getSimpleName() + " not registered.");
        return null;
    }

    // getter

    public Deserializer getDeserializer() {
        return deserializer;
    }

    AttributeMapper getAttributeMapper() {
        return attributeMapper;
    }

    Serializer getSerializer() {
        return serializer;
    }
}
