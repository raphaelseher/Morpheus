package at.rags.morpheus;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * Created by raphaelseher on 09/03/16.
 */
public class MapperUnitTest {

  private Mapper mMapper;

  @Before
  public void setup() {
    mMapper = new Mapper();
  }

  @Test
  public void testInit() throws Exception {
    Mapper mapper = new Mapper();
    assertNotNull(mapper);
  }

  @Test
  public void testMapLinks() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    when(jsonObject.getString("self")).thenReturn("www.self.com");

    Links links = mMapper.mapLinks(jsonObject);

    assertTrue(links.selfLink.equals("www.self.com"));
  }
}
