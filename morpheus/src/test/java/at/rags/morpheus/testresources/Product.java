package at.rags.morpheus.testresources;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

import at.rags.morpheus.Resource;

/**
 * Created by raphaelseher on 08/03/16.
 */
public class Product extends Resource {
    @SerializedName("product-name")
    private String name;
    private List<String> categories;
    private double price;
    @SerializedName("in-stock")
    private int inStock;
    @SerializedName("stores-availability")
    private HashMap<String, Boolean> availability;
    private Location location;
    private List<Author> authors;
    private String[] times;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public HashMap<String, Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(HashMap<String, Boolean> availability) {
        this.availability = availability;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String[] getTimes() {
        return times;
    }

    public void setTimes(String[] times) {
        this.times = times;
    }
}
