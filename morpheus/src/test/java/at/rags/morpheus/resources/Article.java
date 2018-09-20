package at.rags.morpheus.resources;

import android.util.ArrayMap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import at.rags.morpheus.Resource;
import at.rags.morpheus.annotations.Relationship;

public class Article extends Resource {

    private String title;
    @SerializedName("public")
    private boolean publicStatus;
    private List<String> tags;
    private ArrayMap<String, String> map;
    private int version;
    private double price;

    @Relationship("author")
    private Author author;

    @Relationship("authors")
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

    public void setPrice(double price) {
        this.price = price;
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

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
