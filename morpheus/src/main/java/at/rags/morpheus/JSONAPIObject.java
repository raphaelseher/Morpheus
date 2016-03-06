package at.rags.morpheus;

import android.util.ArrayMap;

import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

/**
 * Created by raphaelseher on 05/03/16.
 */
public class JSONAPIObject {

  private Object resource;
  private List<Object> resources;
  private List<Object> included;
  private ArrayMap<String, Object> meta;
  private List<Objects> errors;

  //getters & setters

  public Object getResource() {
    return resource;
  }

  public void setResource(Object resource) {
    this.resource = resource;
  }

  public List<Object> getResources() {
    return resources;
  }

  public void setResources(List<Object> resources) {
    this.resources = resources;
  }

  public List<Object> getIncluded() {
    return included;
  }

  public void setIncluded(List<Object> included) {
    this.included = included;
  }

  public ArrayMap<String, Object> getMeta() {
    return meta;
  }

  public void setMeta(ArrayMap<String, Object> meta) {
    this.meta = meta;
  }

  public List<Objects> getErrors() {
    return errors;
  }

  public void setErrors(List<Objects> errors) {
    this.errors = errors;
  }
}
