package at.rags.morpheus;

import android.util.ArrayMap;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JsonApiObject {

  private Resource resource;
  private List<Resource> resources;
  private List<Resource> included;
  private HashMap<String, Object> meta;
  private List<Error> errors;
  private Links links;

  //getters & setters

  public Object getResource() {
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

  public HashMap<String, Object> getMeta() {
    return meta;
  }

  public void setMeta(HashMap<String, Object> meta) {
    this.meta = meta;
  }

  public List<Error> getErrors() {
    return errors;
  }

  public void setErrors(List<Error> errors) {
    this.errors = errors;
  }

  public Links getLinks() {
    return links;
  }

  public void setLinks(Links links) {
    this.links = links;
  }
}
