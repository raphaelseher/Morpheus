package at.rags.morpheus.Resources;

import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.Resource;

/**
 * Created by raphaelseher on 06/03/16.
 */
public class Comment extends Resource {
  @SerializeName("body")
  private String body;

  public String getBody() {
    return body;
  }
}
