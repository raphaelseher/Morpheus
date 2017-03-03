package at.rags.morpheus;

import java.util.ArrayList;
import java.util.HashMap;

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
public class Resource {
  private String id;
  private at.rags.morpheus.Links links;
  private HashMap<String, Object> meta;

  private ArrayList<String> nullableRelationships = new ArrayList<>();

  public Resource() {
  }

  public HashMap<String, Object> getMeta() {
    return meta;
  }

  public void setMeta(HashMap<String, Object> meta) {
    this.meta = meta;
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

  public ArrayList<String> getNullableRelationships() {
    return nullableRelationships;
  }

  public void resetNullableRelationships() {
    nullableRelationships.clear();
  }

  /**
   * Add here your relationship name, if you want to null it while serializing.
   * This can be used to remove relationships from your object.
   *
   * @param relationshipName Name of your relationship.
   */
  public void addRelationshipToNull(String relationshipName) {
    if (relationshipName == null) {
      return;
    }

    nullableRelationships.add(relationshipName);
  }
}

