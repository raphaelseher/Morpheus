package at.rags.morpheus;

/**
 * Created by raphaelseher on 03/03/16.
 */
public class MorpheusResource {
  private String Id;

  private Links links;

  public String getId() {
    return Id;
  }

  public Links getLinks() {
    return links;
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
