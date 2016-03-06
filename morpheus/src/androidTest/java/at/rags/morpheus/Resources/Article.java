package at.rags.morpheus.Resources;

import java.util.List;

import at.rags.morpheus.Annotations.Relationship;
import at.rags.morpheus.MorpheusResource;
import at.rags.morpheus.Annotations.SerializeName;

public class Article extends MorpheusResource {

  @SerializeName(jsonName = "title")
  private String title;

  @Relationship(relationName = "author")
  private Author author;

  @Relationship(relationName = "comments")
  private List<Comment> comments;

  public String getTitle() {
    return title;
  }

  public Author getAuthor() {
    return author;
  }

  public List<Comment> getComments() {
    return comments;
  }
}
