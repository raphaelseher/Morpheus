package at.rags.morpheusexample.JsonApiResources;

import com.google.gson.annotations.SerializedName;

public class Article extends MyResource {

  @SerializedName(value = "title")
  private String title;

  public String getTitle() {
    return title;
  }
}
