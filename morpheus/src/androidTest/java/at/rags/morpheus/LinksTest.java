package at.rags.morpheus;

import android.os.Bundle;
import android.os.Parcel;
import android.test.InstrumentationTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

/**
 * Created by raphaelseher on 22/04/16.
 */
@RunWith(JUnit4.class)
public class LinksTest extends InstrumentationTestCase {

  @Test
  public void testParcable() {
    Links links = new Links();
    links.setAbout("about");
    links.setFirst("first");
    links.setLast("last");
    links.setNext("next");
    links.setPrev("prev");
    links.setRelated("related");
    links.setSelfLink("self");

    Parcel parcel = Parcel.obtain();
    links.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Links parceledLink = Links.CREATOR.createFromParcel(parcel);

    assertEquals(parceledLink.getAbout(), "about");
    assertEquals(parceledLink.getFirst(), "first");
    assertEquals(parceledLink.getLast(), "last");
    assertEquals(parceledLink.getNext(), "next");
    assertEquals(parceledLink.getPrev(), "prev");
    assertEquals(parceledLink.getRelated(), "related");
    assertEquals(parceledLink.getSelfLink(), "self");
  }
}
