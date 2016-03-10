package at.rags.morpheus.TestResources;

import android.util.ArrayMap;

import java.util.List;

import at.rags.morpheus.Annotations.Relationship;
import at.rags.morpheus.MorpheusResource;
import at.rags.morpheus.Annotations.SerializeName;

public class Article extends MorpheusResource {

  private String title;
  @SerializeName(jsonName = "public")
  private boolean publicStatus;
  private List<String> tags;
  private ArrayMap<String, String> map;
  private int version;
  private double price;

  @Relationship(relationName = "author")
  private Author author;

  @Relationship(relationName = "authors")
  private List<Author> authors;

  //getter & setter

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean getPublicStatus() {
    return publicStatus;
  }

  public void setPublicStatus(boolean publicStatus) {
    this.publicStatus = publicStatus;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public double getPrice() {
    return price;
  }

  public ArrayMap<String, String> getMap() {
    return map;
  }

  public void setMap(ArrayMap<String, String> map) {
    this.map = map;
  }

  public Author getAuthor() {
    return author;
  }

  public List<Author> getAuthors() {
    return authors;
  }
}
