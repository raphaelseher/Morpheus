package at.rags.morpheus.Resources;

import com.google.gson.annotations.SerializedName;

import at.rags.morpheus.Resource;

/**
 * Created by raphaelseher on 06/03/16.
 */
public class Comment extends Resource {
  @SerializedName("body")
  private String body;

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
