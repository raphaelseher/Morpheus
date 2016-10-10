package at.rags.morpheus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Links object.
 *
 * @see JsonApiObject
 * @see Resource
 * @author kwaky
 */
public class Links {
  private String selfLink;
  private String related; // TODO: related can also have a meta
  private String first;
  private String last;
  private String prev;
  private String next;
  private String about;

  public Links() {
  }

  public String getSelfLink() {
    return selfLink;
  }

  public void setSelfLink(String selfLink) {
    this.selfLink = selfLink;
  }

  public String getRelated() {
    return related;
  }

  public void setRelated(String related) {
    this.related = related;
  }

  public String getFirst() {
    return first;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  public String getLast() {
    return last;
  }

  public void setLast(String last) {
    this.last = last;
  }

  public String getPrev() {
    return prev;
  }

  public void setPrev(String prev) {
    this.prev = prev;
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }
}
