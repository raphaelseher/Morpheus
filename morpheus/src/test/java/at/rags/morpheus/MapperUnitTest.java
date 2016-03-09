package at.rags.morpheus;

import android.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import at.rags.morpheus.Annotations.SerializeName;
import at.rags.morpheus.Exceptions.NotExtendingResourceException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    when(jsonObject.getString("related")).thenReturn("www.related.com");
    when(jsonObject.getString("first")).thenReturn("www.first.com");
    when(jsonObject.getString("last")).thenReturn("www.last.com");
    when(jsonObject.getString("prev")).thenReturn("www.prev.com");
    when(jsonObject.getString("next")).thenReturn("www.next.com");

    Links links = mMapper.mapLinks(jsonObject);

    assertTrue(links.selfLink.equals("www.self.com"));
    assertTrue(links.related.equals("www.related.com"));
    assertTrue(links.first.equals("www.first.com"));
    assertTrue(links.last.equals("www.last.com"));
    assertTrue(links.prev.equals("www.prev.com"));
    assertTrue(links.next.equals("www.next.com"));
  }

  @Test
  public void testMapLinksJSONException() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    when(jsonObject.getString("self")).thenThrow(new JSONException(""));
    when(jsonObject.getString("related")).thenThrow(new JSONException(""));
    when(jsonObject.getString("first")).thenThrow(new JSONException(""));
    when(jsonObject.getString("last")).thenThrow(new JSONException(""));
    when(jsonObject.getString("prev")).thenThrow(new JSONException(""));
    when(jsonObject.getString("next")).thenThrow(new JSONException(""));

    Links links = mMapper.mapLinks(jsonObject);

    assertNull(links.selfLink);
    assertNull(links.related);
    assertNull(links.first);
    assertNull(links.last);
    assertNull(links.prev);
    assertNull(links.next);
  }

  @Test
  public void testJsonObjectToArrayMapWithoutData() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    when(jsonObject.keys()).thenReturn(new Iterator<String>() {
      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public String next() {
        return null;
      }

      @Override
      public void remove() {

      }
    });

    ArrayMap<String, Object> map = mMapper.jsonObjectToArrayMap(jsonObject);

    assertNotNull(map);
    assertTrue(map.size() == 0);
  }

  @Test
   public void testJsonObjectToArrayMapWithData() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Iterator mockIter = mock(Iterator.class);
    when(mockIter.hasNext()).thenReturn(true, true, false);
    when(mockIter.next()).thenReturn("String 1", "String 2");
    when(jsonObject.keys()).thenReturn(mockIter);

    ArrayMap<String, Object> map = mMapper.jsonObjectToArrayMap(jsonObject);

    verify(jsonObject).get(eq("String 1"));
    verify(jsonObject).get(eq("String 2"));

    assertNotNull(map);
  }

  @Test
  public void testJsonObjectToArrayMapException() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Iterator mockIter = mock(Iterator.class);

    when(mockIter.hasNext()).thenReturn(true, true, false);
    when(mockIter.next()).thenReturn("String 1", "String 2");
    when(jsonObject.keys()).thenReturn(mockIter);
    when(jsonObject.get(anyString())).thenThrow(new JSONException(""));

    ArrayMap<String, Object> map = mMapper.jsonObjectToArrayMap(jsonObject);

    assertNotNull(map);
  }

  @Test
  public void testMapId() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer);
    JSONObject jsonObject = mock(JSONObject.class);
    MorpheusResource resource = new MorpheusResource();
    resource.setId("123456");

    when(mockDeserializer.
        setIdField(Matchers.<MorpheusResource>anyObject(), anyObject()))
        .thenReturn(resource);

    resource = mapper.mapId(resource, jsonObject);

    assertTrue(resource.getId().equals("123456"));
  }


  @Test(expected = NotExtendingResourceException.class)
  public void testMapIdNotExtendingException() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer);
    JSONObject jsonObject = mock(JSONObject.class);
    MorpheusResource resource = new MorpheusResource();

    when(mockDeserializer.
        setIdField(Matchers.<MorpheusResource>anyObject(), anyObject()))
        .thenThrow(new NotExtendingResourceException(""));

    resource = mapper.mapId(resource, jsonObject);
  }

  @Test()
  public void testMapIdJSONException() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    MorpheusResource resource = new MorpheusResource();

    when(jsonObject.get(anyString())).thenThrow(new JSONException(""));

    resource = mMapper.mapId(resource, jsonObject);

    assertNull(resource.getId());
  }


}
