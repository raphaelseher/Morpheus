package at.rags.morpheus.Resources;

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
  private List<String> times;

  public String getName() {
    return name;
  }

  public List<String> getCategories() {
    return categories;
  }

  public double getPrice() {
    return price;
  }

  public int getInStock() {
    return inStock;
  }

  public HashMap<String, Boolean> getAvailability() {
    return availability;
  }

  public Location getLocation() {
    return location;
  }

  public List<Author> getAuthors() {
    return authors;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setInStock(int inStock) {
    this.inStock = inStock;
  }

  public void setAvailability(HashMap<String, Boolean> availability) {
    this.availability = availability;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public void setAuthors(List<Author> authors) {
    this.authors = authors;
  }

  public List<String> getTimes() {
    return times;
  }

  public void setTimes(List<String> times) {
    this.times = times;
  }
}
