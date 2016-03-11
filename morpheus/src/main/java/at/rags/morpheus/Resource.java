package at.rags.morpheus;

import android.util.ArrayMap;

/**
 * Extend this resource to your custom Object you want to map.
 * You can set custom json object names and relationships via the provided annotations.
 * <pre>
 * {@code
 * public class Article extends Resource { ... }
 * }</pre>
 *
 * @see {@link at.rags.morpheus.Annotations.SerializeName}
 * @see {@link at.rags.morpheus.Annotations.Relationship}
 */
public class Resource {
  private String Id;

  private Links links;

  private ArrayMap<String, Object> meta;

  public ArrayMap<String, Object> getMeta() {
    return meta;
  }

  public void setMeta(ArrayMap<String, Object> meta) {
    this.meta = meta;
  }

  public Links getLinks() {
    return links;
  }

  public void setLinks(Links links) {
    this.links = links;
  }

  public String getId() {
    return Id;
  }

  public void setId(String id) {
    Id = id;
  }
}

class Links {
  public String selfLink;
  public String related;
  public String first;
  public String last;
  public String prev;
  public String next;
  public String about;
}
