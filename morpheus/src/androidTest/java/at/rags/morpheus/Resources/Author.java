package at.rags.morpheus.Resources;

import com.google.gson.annotations.SerializedName;

import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.Resource;

/**
 * Created by raphaelseher on 05/03/16.
 */
public class Author extends Resource {

  @SerializeName("first-name")
  @SerializedName("first-name")
  private String firstName;

  @SerializeName("last-name")
  private String lastName;

  @SerializeName("twitter")
  private String twitterHandle;

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getTwitterHandle() {
    return twitterHandle;
  }
}
