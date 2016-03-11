package at.rags.morpheus;

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

  public String getId() {
    return Id;
  }

  public Links getLinks() {
    return links;
  }

  public void setId(String id) {
    Id = id;
  }

  public void setLinks(Links links) {
    this.links = links;
  }
}

class Links {
  public String selfLink;
  public String related;
  public String first;
  public String last;
  public String prev;
  public String next;
}
