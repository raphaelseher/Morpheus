package at.rags.morpheus;

import android.util.ArrayMap;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import at.rags.morpheus.resources.Article;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by raphaelseher on 10/03/16.
 */
public class AttributeMapperUnitTest {

  private AttributeMapper mAttributeMapper;

  @Before
  public void setup() {
    mAttributeMapper = new AttributeMapper();
  }

  @Test
  public void testJsonObjectToArrayMapWithData() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Iterator mockIter = mock(Iterator.class);
    when(mockIter.hasNext()).thenReturn(true, true, false);
    when(mockIter.next()).thenReturn("String 1", "String 2");
    when(jsonObject.keys()).thenReturn(mockIter);

    HashMap<String, Object> map = mAttributeMapper.createMapFromJSONObject(jsonObject);

    verify(jsonObject).get(eq("String 1"));
    verify(jsonObject).get(eq("String 2"));

    assertNotNull(map);
  }

  @Test
  public void testmapAttributeToObjectWithString() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Deserializer mockDeserializer = mock(Deserializer.class);
    AttributeMapper attributeMapper = new AttributeMapper(mockDeserializer, new Gson());
    JSONArray jsonArray = mock(JSONArray.class);

    when(jsonObject.get("title")).thenReturn("My title");

    Article article = new Article();
    Field field = Article.class.getDeclaredField("title");
    attributeMapper.mapAttributeToObject(article, null, jsonObject, field, "title");

    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

    verify(mockDeserializer).setField(Matchers.<Resource>anyObject(), any(Class.class), eq("title"), stringArgumentCaptor.capture());
  }

  @Test
  public void testmapAttributeToObjectJSONException() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Deserializer mockDeserializer = mock(Deserializer.class);
    AttributeMapper attributeMapper = new AttributeMapper(mockDeserializer, new Gson());
    JSONArray jsonArray = mock(JSONArray.class);

    when(jsonArray.length()).thenReturn(3);
    when(jsonArray.get(0)).thenReturn("Tag1");
    when(jsonArray.get(1)).thenReturn("Tag2");
    when(jsonArray.get(2)).thenThrow(new JSONException(""));
    when(jsonObject.get("tags")).thenReturn(new JSONArray());
    when(jsonObject.getJSONArray("tags")).thenThrow(new JSONException(""));

    Article article = new Article();
    Field field = Article.class.getDeclaredField("tags");
    attributeMapper.mapAttributeToObject(article, null, jsonObject, field, "tags");
  }

  @Test
  public void testmapAttributeToObjectJSONArray() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Deserializer mockDeserializer = mock(Deserializer.class);
    AttributeMapper attributeMapper = new AttributeMapper(mockDeserializer, new Gson());
    JSONArray jsonArray = mock(JSONArray.class);

    when(jsonArray.length()).thenReturn(3);
    when(jsonArray.get(0)).thenReturn("Tag1");
    when(jsonArray.get(1)).thenReturn("Tag2");
    when(jsonArray.get(2)).thenThrow(new JSONException(""));
    when(jsonObject.get("tags")).thenReturn(new JSONArray());
    when(jsonObject.getJSONArray("tags")).thenReturn(jsonArray);

    Article article = new Article();
    Field field = Article.class.getDeclaredField("tags");
    attributeMapper.mapAttributeToObject(article, null, jsonObject, field, "tags");

    ArgumentCaptor<ArrayList> listArgumentCaptor = ArgumentCaptor.forClass(ArrayList.class);

    verify(mockDeserializer).setField(Matchers.<Resource>anyObject(), Matchers.<Class<ArrayList>>anyObject(), eq("tags"), listArgumentCaptor.capture());
    assertTrue(listArgumentCaptor.getValue().get(0).equals("Tag1"));
    assertTrue(listArgumentCaptor.getValue().get(1).equals("Tag2"));
  }

  @Test
  public void testmapAttributeToObjectJSONObject() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Deserializer mockDeserializer = mock(Deserializer.class);
    AttributeMapper attributeMapper = new AttributeMapper(mockDeserializer, new Gson());
    JSONObject mockJSONObject = mock(JSONObject.class);
    Iterator mockIter = mock(Iterator.class);

    when(jsonObject.get("map")).thenReturn(new JSONObject());
    when(jsonObject.getJSONObject("map")).thenReturn(mockJSONObject);

    when(mockIter.hasNext()).thenReturn(true, true, false);
    when(mockIter.next()).thenReturn("Key 1", "Key 2");
    when(mockJSONObject.keys()).thenReturn(mockIter);
    when(mockJSONObject.get(anyString())).thenReturn("String");

    Article article = new Article();
    Field field = Article.class.getDeclaredField("map");
    attributeMapper.mapAttributeToObject(article, null, jsonObject, field, "map");

    ArgumentCaptor<ArrayMap> mapArgumentCaptor = ArgumentCaptor.forClass(ArrayMap.class);

    verify(mockDeserializer).setField(Matchers.<Resource>anyObject(), Matchers.<Class<Article>>anyObject(), eq("map"), mapArgumentCaptor.capture());

    assertNotNull(mapArgumentCaptor);
  }

  @Test
  public void testJsonObjectToArrayMapException() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Iterator mockIter = mock(Iterator.class);

    when(mockIter.hasNext()).thenReturn(true, true, false);
    when(mockIter.next()).thenReturn("String 1", "String 2");
    when(jsonObject.keys()).thenReturn(mockIter);
    when(jsonObject.get(anyString())).thenThrow(new JSONException(""));

    HashMap<String, Object> map = mAttributeMapper.createMapFromJSONObject(jsonObject);

    assertNotNull(map);
  }
}
