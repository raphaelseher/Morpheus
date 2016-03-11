package at.rags.morpheus.Resources;

import java.util.List;

import at.rags.morpheus.Annotations.Relationship;
import at.rags.morpheus.Resource;
import at.rags.morpheus.Annotations.SerializeName;

public class Article extends Resource {

  @SerializeName("title")
  private String title;

  @Relationship("author")
  private Author author;

  @Relationship("comments")
  private List<Comment> comments;

  private List<String> tags;

  public String getTitle() {
    return title;
  }

  public Author getAuthor() {
    return author;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public List<String> getTags() {
    return tags;
  }
}
