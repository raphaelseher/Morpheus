package at.rags.morpheus;

import android.os.Parcel;
import android.test.InstrumentationTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;

/**
 * Created by raphaelseher on 22/04/16.
 */
@RunWith(JUnit4.class)
public class ResourceTest extends InstrumentationTestCase {

  @Test
  public void testParcable() {
    Resource resource = new Resource();
    resource.setId("123");
    HashMap<String, Object> meta = new HashMap<>();
    meta.put("test", true);
    resource.setMeta(meta);

    Parcel parcel = Parcel.obtain();
    resource.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Resource parcelResource= Resource.CREATOR.createFromParcel(parcel);

    assertEquals(parcelResource.getId(), "123");
    assertTrue((Boolean) parcelResource.getMeta().get("test"));
  }
}
