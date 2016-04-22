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
public class Links implements Parcelable {
  private String selfLink;
  private String related;
  private String first;
  private String last;
  private String prev;
  private String next;
  private String about;

  public Links() {
  }

  protected Links(Parcel in) {
    selfLink = in.readString();
    related = in.readString();
    first = in.readString();
    last = in.readString();
    prev = in.readString();
    next = in.readString();
    about = in.readString();
  }

  public static final Creator<Links> CREATOR = new Creator<Links>() {
    @Override
    public Links createFromParcel(Parcel in) {
      return new Links(in);
    }

    @Override
    public Links[] newArray(int size) {
      return new Links[size];
    }
  };

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(selfLink);
    dest.writeString(related);
    dest.writeString(first);
    dest.writeString(last);
    dest.writeString(prev);
    dest.writeString(next);
    dest.writeString(about);
  }
}
