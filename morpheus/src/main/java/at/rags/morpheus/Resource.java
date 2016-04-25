package at.rags.morpheus;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.HashMap;
import java.util.Objects;

/**
 * Extend this resource to your custom Object you want to map.
 * You can set custom json object names and relationships via the provided annotations.
 * <pre>
 * {@code
 * public class Article extends Resource { ... }
 * }</pre>
 *
 * @see com.google.gson.annotations.SerializedName
 * @see at.rags.morpheus.Annotations.Relationship
 */
public class Resource {
  private String Id;
  private Links links;
  private HashMap<String, Object> meta;

  public Resource() {
  }

  public HashMap<String, Object> getMeta() {
    return meta;
  }

  public void setMeta(HashMap<String, Object> meta) {
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

