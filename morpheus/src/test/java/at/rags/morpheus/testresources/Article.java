package at.rags.morpheus.testresources;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import at.rags.morpheus.Resource;
import at.rags.morpheus.annotations.JsonApiType;
import at.rags.morpheus.annotations.Relationship;

@JsonApiType("articles")
public class Article extends Resource {

    @SerializedName("title")
    private String title;

    @Relationship("author")
    private Author author;

    @Relationship("comments")
    private List<Comment> comments;

    private List<String> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
