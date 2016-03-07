package at.rags.morpheus.Resources;

import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.MorpheusResource;

/**
 * Created by raphaelseher on 05/03/16.
 */
public class Author extends MorpheusResource {

  @SerializeName(jsonName = "first-name")
  private String firstName;

  @SerializeName(jsonName = "last-name")
  private String lastName;

  @SerializeName(jsonName = "twitter")
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
