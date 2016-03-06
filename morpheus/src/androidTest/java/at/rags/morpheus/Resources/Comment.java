package at.rags.morpheus.Resources;

import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.MorpheusResource;

/**
 * Created by raphaelseher on 06/03/16.
 */
public class Comment extends MorpheusResource {
  @SerializeName(jsonName = "body")
  private String body;

}
