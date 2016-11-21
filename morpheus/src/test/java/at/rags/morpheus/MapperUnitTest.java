package at.rags.morpheus;

import android.util.ArraySet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import at.rags.morpheus.Exceptions.NotExtendingResourceException;
import at.rags.morpheus.TestResources.Article;
import at.rags.morpheus.TestResources.Author;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapperUnitTest {

  private Mapper mapper;

  @Before
  public void setup() {
    mapper = new Mapper();
    Deserializer.setRegisteredClasses(new HashMap<String, Class>());
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

    Links links = mapper.mapLinks(jsonObject);

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

    Links links = mapper.mapLinks(jsonObject);

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
    Mapper mapper = new Mapper(mockDeserializer, null, null);
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
    Mapper mapper = new Mapper(mockDeserializer, null, null);
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

    resource = mapper.mapId(resource, jsonObject);

    assertNull(resource.getId());
  }

  @Test
  public void testMapAttributesMapping() throws Exception {
    JSONObject jsonObject = mock(JSONObject.class);
    JSONArray jsonArray = mock(JSONArray.class);
    AttributeMapper mockAttributeMapper = mock(AttributeMapper.class);
    Mapper mapper = new Mapper(new Deserializer(), new Serializer(), mockAttributeMapper);

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
    Mapper mapper = new Mapper(mockDeserializer, null, null);
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
    Mapper mapper = new Mapper(mockDeserializer, null, null);
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
    Mapper mapper = new Mapper(mockDeserializer, null, null);
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
    Mapper mapper = new Mapper(mockDeserializer,null, null);
    Factory.setDeserializer(mockDeserializer);
    Factory.setMapper(mapper);
    JSONObject jsonObject = mock(JSONObject.class);
    JSONObject relationObject =  mock(JSONObject.class);
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

  @Test
  public void testCreateDataFromJsonResources() {
    Deserializer.registerResourceClass("authors", Author.class);

    Author author = new Author();
    author.setId("id");
    author.setName("name");
    ArrayList<Author> authors = new ArrayList<>();
    authors.add(author);
    authors.add(author);

    HashMap<String, Object> authorMap = new HashMap<>();
    authorMap.put("id","id");
    authorMap.put("type", "authors");
    authorMap.put("attributes", mapper.getSerializer().getFieldsAsDictionary(author));

    ArrayList<HashMap<String, Object>> dataArray = new ArrayList<>();
    dataArray.add(authorMap);
    dataArray.add(authorMap);

    HashMap<String, ArrayList> checkData = new HashMap<>();
    checkData.put("data", dataArray);

    HashMap output = mapper
        .createDataFromJsonResources((List)authors, true);

    assertNotNull(output);
    assertEquals(output.toString(), checkData.toString());
  }

  @Test
  public void testCreateDataFromJsonResource() {
    Deserializer.registerResourceClass("authors", Author.class);

    Author author = new Author();
    author.setId("id");
    author.setName("name");

    HashMap<String, Object> authorMap = new HashMap<>();
    authorMap.put("id","id");
    authorMap.put("type", "authors");
    authorMap.put("attributes", mapper.getSerializer().getFieldsAsDictionary(author));

    HashMap<String, Object> checkData = new HashMap<>();
    checkData.put("data", authorMap);

    HashMap<String, Object> output = mapper
        .createDataFromJsonResource(author, true);

    assertNotNull(output);
    assertEquals(output.toString(), checkData.toString());
  }

  @Test
  public void testCreateDataFromJsonResourceWithRelationship() {
    Deserializer.registerResourceClass("authors", Author.class);
    Deserializer.registerResourceClass("articles", Article.class);

    Author author = new Author();
    author.setId("id");
    author.setName("name");

    Article article = new Article();
    article.setId("articleId");
    article.setTitle("Some title");
    article.setPrice(1.0);
    article.setPublicStatus(true);
    article.setMap(null);
    article.setVersion(10);
    article.setTags(null);
    article.setAuthor(author);

    HashMap<String, Object> relationMap = new HashMap<>();
    relationMap.put("author", mapper.createDataFromJsonResource(author, false));

    HashMap<String, Object> articleMap = new HashMap<>();
    articleMap.put("id","articleId");
    articleMap.put("type", "articles");
    articleMap.put("attributes", mapper.getSerializer().getFieldsAsDictionary(article));
    articleMap.put("relationships", relationMap);

    HashMap<String, Object> checkData = new HashMap<>();
    checkData.put("data", articleMap);

    HashMap<String, Object> output = mapper
        .createDataFromJsonResource(article, true);

    assertNotNull(output);
    assertEquals(output.toString(), checkData.toString());
  }

  @Test
  public void testCreateDataFromJsonResourceWithLinks() {
    Deserializer.registerResourceClass("authors", Author.class);

    Author author = new Author();
    author.setId("id");

    Links links = new Links();
    links.setSelfLink("self.com");
    links.setRelated("related.com");
    author.setLinks(links);

    HashMap<String, Object> linkMap = new HashMap<>();
    linkMap.put("self", "self.com");
    linkMap.put("related", "related.com");

    HashMap<String, Object> authorMap = new HashMap<>();
    authorMap.put("id","id");
    authorMap.put("type", "authors");
    authorMap.put("links", linkMap);

    HashMap<String, Object> checkData = new HashMap<>();
    checkData.put("data", authorMap);

    HashMap<String, Object> output = mapper
        .createDataFromJsonResource(author, true);

    assertNotNull(output);
    assertEquals(output.toString(), checkData.toString());
  }

  @Test
  public void testCreateRelationshipsFromResource() {
    Deserializer.registerResourceClass("authors", Author.class);
    Deserializer.registerResourceClass("articles", Article.class);

    Author author = new Author();
    author.setId("authorId");

    Article article = new Article();
    article.setId("articleId");
    article.setTitle("Some title");
    article.setAuthor(author);

    HashMap<String, Object> authorMap = new HashMap<>();

    authorMap.put("author", mapper.createDataFromJsonResource(author, false));

    HashMap<String, Object> output = mapper.createRelationshipsFromResource(article);

    assertNotNull(output);
    assertEquals(authorMap.toString(), output.toString());
  }

  @Test
  public void testCreateRelationshipsFromResourceReturnsNullWithNoRelations() {
    Deserializer.registerResourceClass("authors", Author.class);
    Deserializer.registerResourceClass("articles", Article.class);

    Article article = new Article();
    article.setId("articleId");
    article.setTitle("Some title");

    HashMap<String, Object> output = mapper.createRelationshipsFromResource(article);

    assertEquals(null, output);
  }

  @Test
  public void testCreateLinksFromResource() {
    HashMap<String, Object> checkLinks = new HashMap<>();
    checkLinks.put("self", "selflink.com");
    checkLinks.put("related", "related.com");
    checkLinks.put("first", "first.com");
    checkLinks.put("last", "last.com");
    checkLinks.put("prev", "prev.com");
    checkLinks.put("next", "next.com");
    checkLinks.put("about", "about.com");

    Resource resource = new Resource();
    Links links = new Links();
    links.setSelfLink("selflink.com");
    links.setRelated("related.com");
    links.setFirst("first.com");
    links.setLast("last.com");
    links.setPrev("prev.com");
    links.setNext("next.com");
    links.setAbout("about.com");
    resource.setLinks(links);

    HashMap<String, Object> linksFromResource =
        mapper.createLinksFromResource(resource);

    assertEquals(linksFromResource, checkLinks);
  }
}
