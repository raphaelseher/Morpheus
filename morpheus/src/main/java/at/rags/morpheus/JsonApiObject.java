package at.rags.morpheus;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

public class JsonApiObject {

    private Resource resource;
    private List<Resource> resources;
    private List<Resource> included;
    private JSONObject meta;
    private List<Error> errors;
    private at.rags.morpheus.Links links;

    //getters & setters

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Resource> getIncluded() {
        return included;
    }

    public void setIncluded(List<Resource> included) {
        this.included = included;
    }

    public JSONObject getMeta() {
        return meta;
    }

    public void setMeta(JSONObject meta) {
        this.meta = meta;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public at.rags.morpheus.Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
