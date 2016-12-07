package at.rags.morpheus.Resources;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

import at.rags.morpheus.Resource;

/**
 * Created by raphaelseher on 08/03/16.
 */
public class Product extends Resource {
  @SerializedName("product-name-description")
  private String nameDescription;
  @SerializedName("product-name")
  private String name;
  private List<String> categories;
  private double price;
  @SerializedName("in-stock")
  private int inStock;
  @SerializedName("string-int")
  private int stringInt;
  @SerializedName("stores-availability")
  private HashMap<String, Boolean> availability;
  private boolean available = false;
  private Location location;
  private List<Author> authors;

  public String getNameDescription() {
    return nameDescription;
  }

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

  public int getStringInt() {
    return stringInt;
  }

  public boolean isAvailable() {
    return available;
  }
}
