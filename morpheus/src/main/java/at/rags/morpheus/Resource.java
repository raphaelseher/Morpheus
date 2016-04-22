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
 * @see at.rags.morpheus.Annotations.SerializeName
 * @see at.rags.morpheus.Annotations.Relationship
 */
public class Resource implements Parcelable {
  private String Id;
  private Links links;
  private HashMap<String, Object> meta;

  public Resource() {
  }

  protected Resource(Parcel in) {
    Id = in.readString();
    meta = new HashMap<>();
    in.readMap(meta, Object.class.getClassLoader());
    links = in.readParcelable(Links.class.getClassLoader());
  }

  public static final Creator<Resource> CREATOR = new Creator<Resource>() {
    @Override
    public Resource createFromParcel(Parcel in) {
      return new Resource(in);
    }

    @Override
    public Resource[] newArray(int size) {
      return new Resource[size];
    }
  };

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(Id);
    dest.writeMap(meta);
    dest.writeParcelable(links, flags);
  }
}

