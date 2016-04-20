package at.rags.morpheus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import at.rags.morpheus.Exceptions.NotExtendingResourceException;
import at.rags.morpheus.TestResources.Article;
import at.rags.morpheus.TestResources.Author;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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

    assertTrue(links.getSelfLink().equals("www.self.com"));
    assertTrue(links.getRelated().equals("www.related.com"));
    assertTrue(links.getFirst().equals("www.first.com"));
    assertTrue(links.getLast().equals("www.last.com"));
    assertTrue(links.getPrev().equals("www.prev.com"));
    assertTrue(links.getNext().equals("www.next.com"));
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

    assertNull(links.getSelfLink());
    assertNull(links.getRelated());
    assertNull(links.getFirst());
    assertNull(links.getLast());
    assertNull(links.getPrev());
    assertNull(links.getNext());
  }

  @Test
  public void testMapId() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer, null);
    JSONObject jsonObject = mock(JSONObject.class);
    Resource resource = new Resource();
    resource.setId("123456");

    when(mockDeserializer.
        setIdField(Matchers.<Resource>anyObject(), anyObject()))
        .thenReturn(resource);

    resource = mapper.mapId(resource, jsonObject);

    assertTrue(resource.getId().equals("123456"));
  }


  @Test(expected = NotExtendingResourceException.class)
  public void testMapIdNotExtendingException() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer, null);
    JSONObject jsonObject = mock(JSONObject.class);
    Resource resource = new Resource();

    when(mockDeserializer.
        setIdField(Matchers.<Resource>anyObject(), anyObject()))
        .thenThrow(new NotExtendingResourceException(""));

    resource = mapper.mapId(resource, jsonObject);
  }

  @Test
  public void testMapIdJSONException() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    Resource resource = new Resource();

    when(jsonObject.get(anyString())).thenThrow(new JSONException(""));

    resource = mMapper.mapId(resource, jsonObject);

    assertNull(resource.getId());
  }

  @Test
  public void testMapAttributesMapping() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    JSONArray jsonArray = mock(JSONArray.class);
    AttributeMapper mockAttributeMapper = mock(AttributeMapper.class);
    Mapper mapper = new Mapper(new Deserializer(), mockAttributeMapper);

    Article article = new Article();
    article.setId("1");

    mapper.mapAttributes(article, jsonObject);

    ArgumentCaptor<Field> fieldArgumentCaptor = ArgumentCaptor.forClass(Field.class);

    verify(mockAttributeMapper).mapAttributeToObject(Matchers.<Resource>anyObject(),
        any(JSONObject.class), any(Field.class), eq("title"));
    verify(mockAttributeMapper).mapAttributeToObject(Matchers.<Resource>anyObject(),
        any(JSONObject.class), fieldArgumentCaptor.capture(), eq("public"));
    verify(mockAttributeMapper).mapAttributeToObject(Matchers.<Resource>anyObject(),
        any(JSONObject.class), any(Field.class), eq("tags"));
    verify(mockAttributeMapper).mapAttributeToObject(Matchers.<Resource>anyObject(),
        any(JSONObject.class), any(Field.class), eq("map"));
    verify(mockAttributeMapper).mapAttributeToObject(Matchers.<Resource>anyObject(),
        any(JSONObject.class), any(Field.class), eq("version"));
    verify(mockAttributeMapper).mapAttributeToObject(Matchers.<Resource>anyObject(),
        any(JSONObject.class), any(Field.class), eq("price"));

    assertEquals(fieldArgumentCaptor.getValue().getName(), "publicStatus");
  }

  @Test
  public void testMapRelationsExceptions() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer, null);
    JSONObject jsonObject = mock(JSONObject.class);
    List<Resource> mockIncluded = mock(List.class);

    when(jsonObject.getJSONObject(anyString())).thenThrow(new JSONException(""));

    Article article = new Article();
    mapper.mapRelations(article, jsonObject, mockIncluded);

    assertNull(article.getAuthor());
  }

  @Test
  public void testMapRelationsObjectRelation() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer, null);
    Factory.setDeserializer(mockDeserializer);
    Factory.setMapper(mapper);
    JSONObject jsonObject = mock(JSONObject.class);
    JSONObject relationObject = mock(JSONObject.class);
    JSONObject authorObject = mock(JSONObject.class);
    List<Resource> included = new ArrayList<>();

    Author includedAuthor = new Author();
    includedAuthor.setId("1");
    includedAuthor.setName("James");
    included.add(includedAuthor);

    Author author = new Author();
    author.setId("1");

    when(mockDeserializer.createObjectFromString(anyString())).thenReturn(author);
    when(jsonObject.getJSONObject(eq("author"))).thenReturn(relationObject);
    when(jsonObject.getJSONObject(eq("authors"))).thenThrow(new JSONException(""));
    when(relationObject.getJSONObject(anyString())).thenReturn(authorObject);
    when(relationObject.getJSONArray(anyString())).thenThrow(new JSONException(""));
    when(authorObject.getString("type")).thenReturn("author");
    when(authorObject.getJSONObject("links")).thenReturn(new JSONObject());
    when(mockDeserializer.setIdField(Matchers.<Resource>anyObject(),
        any(JSONObject.class))).thenReturn(author);

    Article article = new Article();
    Article mappedArticle = (Article)mapper.mapRelations(article, jsonObject, included);

    ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);

    verify(mockDeserializer).setField(Matchers.<Resource>anyObject(), eq("author"), objectArgumentCaptor.capture());

    Author resultAuthor = (Author)objectArgumentCaptor.getValue();
    assertEquals(resultAuthor.getId(), "1");
    assertEquals(resultAuthor.getName(), "James");
  }

  @Test
  public void testMapRelationsObjectRelationWithoutInclude() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer, null);
    Factory.setDeserializer(mockDeserializer);
    Factory.setMapper(mapper);
    JSONObject jsonObject = mock(JSONObject.class);
    JSONObject relationObject = mock(JSONObject.class);
    JSONObject authorObject = mock(JSONObject.class);

    Author author = new Author();
    author.setId("1");
    author.setName("Name");

    when(mockDeserializer.createObjectFromString(anyString())).thenReturn(author);
    when(jsonObject.getJSONObject(eq("author"))).thenReturn(relationObject);
    when(jsonObject.getJSONObject(eq("authors"))).thenThrow(new JSONException(""));
    when(relationObject.getJSONObject(anyString())).thenReturn(authorObject);
    when(relationObject.getJSONArray(anyString())).thenThrow(new JSONException(""));
    when(authorObject.getString("type")).thenReturn("author");
    when(authorObject.getJSONObject("links")).thenReturn(new JSONObject());
    when(mockDeserializer.setIdField(Matchers.<Resource>anyObject(),
        any(JSONObject.class))).thenReturn(author);

    Article article = new Article();
    Article mappedArticle = (Article)mapper.mapRelations(article, jsonObject, null);

    ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);

    verify(mockDeserializer).setField(Matchers.<Resource>anyObject(), eq("author"), objectArgumentCaptor.capture());

    Author resultAuthor = (Author)objectArgumentCaptor.getValue();
    assertEquals(resultAuthor.getId(), "1");
    assertEquals(resultAuthor.getName(), "Name");
    assertNotNull(mappedArticle);
  }

  @Test
  public void testMapRelationsObjectsRelation() throws Exception {
    Deserializer mockDeserializer = mock(Deserializer.class);
    Mapper mapper = new Mapper(mockDeserializer, null);
    Factory.setDeserializer(mockDeserializer);
    Factory.setMapper(mapper);
    JSONObject jsonObject = mock(JSONObject.class);
    JSONObject relationObject = mock(JSONObject.class);
    JSONArray authorObjects = mock(JSONArray.class);
    JSONObject authorObject = mock(JSONObject.class);
    List<Resource> included = new ArrayList<>();

    Author includedAuthor = new Author();
    includedAuthor.setId("1");
    includedAuthor.setName("James");
    included.add(includedAuthor);

    Author author1 = new Author();
    author1.setId("1");

    Author author2 = new Author();
    author2.setId("2");

    when(mockDeserializer.createObjectFromString(anyString())).thenReturn(author1);
    when(jsonObject.getJSONObject(anyString())).thenReturn(relationObject);
    when(relationObject.getJSONArray(anyString())).thenReturn(authorObjects);
    when(relationObject.getJSONObject(anyString())).thenThrow(new JSONException(""));
    when(authorObjects.length()).thenReturn(2);
    when(authorObjects.getJSONObject(0)).thenReturn(authorObject);
    when(authorObjects.getJSONObject(1)).thenReturn(authorObject);
    when(authorObject.getJSONObject("links")).thenReturn(new JSONObject());
    when(mockDeserializer.setIdField(Matchers.<Resource>anyObject(),
        any(JSONObject.class))).thenReturn(author1);

    Article article = new Article();
    Article mappedArticle = (Article)mapper.mapRelations(article, jsonObject, included);

    ArgumentCaptor<List> objectArgumentCaptor = ArgumentCaptor.forClass(List.class);

    verify(mockDeserializer).setField(Matchers.<Resource>anyObject(), eq("authors"), objectArgumentCaptor.capture());

    Author resultAuthor = (Author)objectArgumentCaptor.getValue().get(0);
    assertEquals(resultAuthor.getId(), "1");
    assertEquals(resultAuthor.getName(), "James");
  }
}
