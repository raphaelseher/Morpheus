package at.rags.morpheus;

import android.util.ArrayMap;

import java.util.List;
import java.util.Objects;

public class JSONAPIObject {

  private Resource resource;
  private List<Resource> resources;
  private List<Resource> included;
  private ArrayMap<String, Object> meta;
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

  public ArrayMap<String, Object> getMeta() {
    return meta;
  }

  public void setMeta(ArrayMap<String, Object> meta) {
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
