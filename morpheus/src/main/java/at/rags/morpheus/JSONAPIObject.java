package at.rags.morpheus;

import android.util.ArrayMap;

import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

/**
 * Created by raphaelseher on 05/03/16.
 */
public class JSONAPIObject {

  private MorpheusResource resource;
  private List<MorpheusResource> resources;
  private List<MorpheusResource> included;
  private ArrayMap<String, Object> meta;
  private List<Objects> errors;
  private Links links;

  //getters & setters

  public Object getResource() {
    return resource;
  }

  public void setResource(MorpheusResource resource) {
    this.resource = resource;
  }

  public List<MorpheusResource> getResources() {
    return resources;
  }

  public void setResources(List<MorpheusResource> resources) {
    this.resources = resources;
  }

  public List<MorpheusResource> getIncluded() {
    return included;
  }

  public void setIncluded(List<MorpheusResource> included) {
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

  public Links getLinks() {
    return links;
  }

  public void setLinks(Links links) {
    this.links = links;
  }
}
