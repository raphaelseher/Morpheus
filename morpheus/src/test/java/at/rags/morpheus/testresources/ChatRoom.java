package at.rags.morpheus.testresources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import at.rags.morpheus.Resource;
import at.rags.morpheus.annotations.JsonApiType;
import at.rags.morpheus.annotations.Relationship;

@JsonApiType("ChatRoom")
public class ChatRoom extends Resource implements Serializable {

    @SerializedName("pin")
    private String pin;

    @Relationship("articles")
    private List<Article> articles;

    public String getPin() {
        return pin;
    }

    public List<Article> getArticles() {
        return articles;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChatRoom.class, new ResourceSerializer<ChatRoom>())
            .create();
        return gson.toJson(this);
    }
}
