package at.rags.morpheus.Resources;

import android.util.ArrayMap;

import java.util.Dictionary;
import java.util.List;

import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.MorpheusResource;

/**
 * Created by raphaelseher on 08/03/16.
 */
public class Product extends MorpheusResource {
  @SerializeName(jsonName = "product-name")
  private String name;
  private List<String> categories;
  private float price;
  @SerializeName(jsonName = "in-stock")
  private int inStock;
  @SerializeName(jsonName = "stores-availability")
  private ArrayMap<String, Boolean> availability;

  public String getName() {
    return name;
  }

  public List<String> getCategories() {
    return categories;
  }

  public float getPrice() {
    return price;
  }

  public int getInStock() {
    return inStock;
  }

  public ArrayMap<String, Boolean> getAvailability() {
    return availability;
  }
}
