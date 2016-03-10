package at.rags.morpheus.TestResources;

import at.rags.morpheus.Morpheus;
import at.rags.morpheus.MorpheusResource;

/**
 * Created by raphaelseher on 10/03/16.
 */
public class Author extends MorpheusResource {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
