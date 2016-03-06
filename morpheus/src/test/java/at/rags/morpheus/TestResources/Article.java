package at.rags.morpheus.TestResources;

import at.rags.morpheus.MorpheusResource;
import at.rags.morpheus.Annotations.SerializeName;

public class Article extends MorpheusResource {

  @SerializeName(jsonName = "title")
  private String title;

  public String getTitle() {
    return title;
  }
}
