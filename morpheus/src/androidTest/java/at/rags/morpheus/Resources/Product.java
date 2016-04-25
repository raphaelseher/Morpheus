package at.rags.morpheus.Resources;

import android.util.ArrayMap;

import java.util.HashMap;
import java.util.List;

import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.Resource;

/**
 * Created by raphaelseher on 08/03/16.
 */
public class Product extends Resource {
  @SerializeName("product-name")
  private String name;
  private List<String> categories;
  private double price;
  @SerializeName("in-stock")
  private int inStock;
  @SerializeName("stores-availability")
  private HashMap<String, Boolean> availability;
  private Location location;
  private List<Author> authors;

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
}
