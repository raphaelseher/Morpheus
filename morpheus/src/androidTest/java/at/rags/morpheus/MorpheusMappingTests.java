package at.rags.morpheus;

import android.test.InstrumentationTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.rags.morpheus.Resources.Article;
import at.rags.morpheus.Resources.Author;
import at.rags.morpheus.Resources.Comment;
import at.rags.morpheus.Resources.Location;
import at.rags.morpheus.Resources.Product;

@RunWith(JUnit4.class)
public class MorpheusMappingTests extends InstrumentationTestCase {

  @Test
  public void testInit() throws Exception {
    Morpheus morpheus = new Morpheus();
    Logger.setDebug(true);
    assertNotNull(morpheus);
  }

  @Test
  public void testDataArray() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertTrue(jsonApiObject.getResources().size() == 1);
    assertTrue(jsonApiObject.getResources().get(0).getClass() == Article.class);
    Article article = (Article) jsonApiObject.getResources().get(0);
    assertTrue(article.getId().equals("1"));
    assertTrue(article.getTitle().equals("JSON API paints my bikeshed!"));
  }

  @Test
  public void testDataObject() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonApiObject.getResource());
    assertTrue(jsonApiObject.getResource().getClass() == Article.class);
    Article article = (Article) jsonApiObject.getResource();
    assertTrue(article.getId().equals("1"));
    assertTrue(article.getTitle().equals("JSON API paints my bikeshed!"));
  }

  @Test
  public void testDataObjectMeta() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonApiObject.getResource());
    assertTrue(jsonApiObject.getResource().getClass() == Article.class);
    Article article = (Article) jsonApiObject.getResource();
    assertTrue(article.getId().equals("1"));
    assertTrue(article.getTitle().equals("JSON API paints my bikeshed!"));
    assertNotNull(article.getMeta());
    assertEquals(article.getMeta().get("test-meta"), "yes");
  }


  @Test
  public void testRelationship() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertTrue(jsonApiObject.getResources().size() == 1);
    assertTrue(jsonApiObject.getResources().get(0).getClass() == Article.class);
    Article article = (Article) jsonApiObject.getResources().get(0);
    assertNotNull(article.getAuthor());
    assertTrue(article.getComments().size() == 2);
  }

  @Test
  public void testIncluded() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertTrue(jsonApiObject.getIncluded().size() == 3);
  }

  @Test
  public void testIncludedRelations() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonApiObject.getResource());
    Article article = (Article) jsonApiObject.getResource();
    assertTrue(article.getAuthor().getFirstName().equals("Dan"));

    Comment comment = (Comment)article.getComments().get(0);
    assertTrue(comment.getBody().equals("First!"));
  }

  @Test
  public void testLinks() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertEquals(jsonApiObject.getLinks().getSelfLink(), "http://example.com/articles");
    assertEquals(jsonApiObject.getLinks().getNext(), "http://example.com/articles?page[offset]=2");
    assertEquals(jsonApiObject.getLinks().getLast(), "http://example.com/articles?page[offset]=10");

    assertNotNull(jsonApiObject.getResource());
    Article article = (Article) jsonApiObject.getResource();
    assertNotNull(article.getLinks());
    assertTrue(article.getLinks().getSelfLink().equals("http://example.com/articles/1"));
    assertNull(article.getLinks().getRelated());
  }

  @Test
  public void testPaginationLinks() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonApiObject.getLinks());
    assertTrue(jsonApiObject.getLinks().getSelfLink().equals("http://example.com/articles"));
    assertTrue(jsonApiObject.getLinks().getNext().equals("http://example.com/articles?page[offset]=2"));
    assertTrue(jsonApiObject.getLinks().getLast().equals("http://example.com/articles?page[offset]=10"));
    assertNull(jsonApiObject.getLinks().getRelated());
  }

  @Test
  public void testMeta() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertNotNull(jsonApiObject.getMeta());
    assertTrue(jsonApiObject.getMeta().get("testmeta").equals("yes"));
  }

  @Test
  public void testAttributesArray() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonApiObject.getLinks());
    Article article = (Article) jsonApiObject.getResource();

    assertTrue(article.getTags().get(0).equals("main"));
    assertTrue(article.getTags().get(1).equals("dev"));
  }

  @Test
  public void testAttributesTypes() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("products", Product.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.product));

    Product product = (Product) jsonApiObject.getResources().get(0);

    assertTrue(product.getId().equals("123456"));
    assertTrue(product.getName().equals("Fancy new roboter"));
    assertTrue(product.getPrice() == 999.75);
    assertTrue(product.getInStock() == 9);
    assertTrue(product.getAvailability().get("Store 1"));
    assertFalse(product.getAvailability().get("Store 3"));
    assertEquals(product.getLocation().getLat(), 14.202323);
    assertEquals(product.getLocation().getLon(), 12.04995);
    assertEquals(product.getAuthors().size(), 1);
    assertEquals(product.getAuthors().get(0).getClass(), Author.class);
    assertEquals(product.getAuthors().get(0).getFirstName(), "raphael");
    assertEquals(product.getTimes().get(0), "9:14");
    assertEquals(product.getTimes().get(1), "12 15");

  }

  @Test
  public void testErrors() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("products", Product.class);

    JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.error));

    assertNotNull(jsonApiObject.getErrors());
    assertEquals(jsonApiObject.getErrors().get(0).getStatus(), "403");
    assertEquals(jsonApiObject.getErrors().get(0).getSource().getPointer(), "/data/attributes/secret-powers");
    assertEquals(jsonApiObject.getErrors().get(0).getDetail(), "Editing secret powers is not authorized on Sundays.");

    assertEquals(jsonApiObject.getErrors().get(1).getStatus(), "422");
    assertEquals(jsonApiObject.getErrors().get(1).getId(), "1");
    assertEquals(jsonApiObject.getErrors().get(1).getCode(), "2");
    assertEquals(jsonApiObject.getErrors().get(1).getSource().getPointer(), "/data/attributes/volume");
    assertEquals(jsonApiObject.getErrors().get(1).getSource().getParameter(), "/data/attributes/battery");
    assertEquals(jsonApiObject.getErrors().get(1).getTitle(), "some title");
    assertEquals(jsonApiObject.getErrors().get(1).getDetail(), "Volume does not, in fact, go to 11.");
    assertEquals(jsonApiObject.getErrors().get(1).getLinks().getAbout(), "about.com");

    assertEquals(jsonApiObject.getErrors().get(2).getStatus(), "500");
    assertEquals(jsonApiObject.getErrors().get(2).getSource().getPointer(), "/data/attributes/reputation");
    assertNull(jsonApiObject.getErrors().get(2).getSource().getParameter());
    assertEquals(jsonApiObject.getErrors().get(2).getTitle(), "The backend responded with an error");
    assertEquals(jsonApiObject.getErrors().get(2).getDetail(), "Reputation service not responding after three requests.");
  }

  @Test
  public void testCreateJsonWithResourceRelationsIncluded() {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    String checkJson = "{\"included\":[{\"attributes\":{\"body\":\"body\"},\"id\":\"3\",\"type\":\"comments\"},{\"attributes\":{\"body\":\"body\"},\"id\":\"3\",\"type\":\"comments\"},{\"attributes\":{\"first-name\":\"Peter\"},\"id\":\"2\",\"type\":\"people\"}],\"data\":{\"attributes\":{\"title\":\"Some title\"},\"id\":\"1\",\"type\":\"articles\",\"relationships\":{\"comments\":{\"data\":[{\"id\":\"3\",\"type\":\"comments\"},{\"id\":\"3\",\"type\":\"comments\"}]},\"author\":{\"data\":{\"id\":\"2\",\"type\":\"people\"}}}}}";

    Article article = new Article();
    article.setId("1");
    article.setTitle("Some title");

    Author author = new Author();
    author.setId("2");
    author.setFirstName("Peter");
    article.setAuthor(author);

    Comment comment = new Comment();
    comment.setId("3");
    comment.setBody("body");

    ArrayList<Comment> comments = new ArrayList<>();
    comments.add(comment);
    comments.add(comment);
    article.setComments(comments);

    JsonApiObject jsonApiObject = new JsonApiObject();
    jsonApiObject.setResource(article);


    String json = morpheus.createJson(jsonApiObject, true);


    assertEquals(json, checkJson);
  }

  @Test
  public void testCreateJsonWithResourcesRelations() {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    String checkJson = "{\"data\":[{\"attributes\":{\"title\":\"Some title\"},\"id\":\"1\",\"type\":\"articles\"},{\"attributes\":{\"title\":\"Some title\"},\"id\":\"1\",\"type\":\"articles\"}]}";

    Article article = new Article();
    article.setId("1");
    article.setTitle("Some title");

    ArrayList<Resource> articles = new ArrayList<>();
    articles.add(article);
    articles.add(article);

    JsonApiObject jsonApiObject = new JsonApiObject();
    jsonApiObject.setResources(articles);


    String json = morpheus.createJson(jsonApiObject, false);


    assertEquals(json, checkJson);
  }

  @Test
  public void testCreateJsonAttributes() {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("products", Product.class);

    String checkJson = "{\"data\":{\"attributes\":{\"stores-availability\":{\"there\":false,\"here\":true},\"price\":10.3,\"in-stock\":10,\"location\":{\"lat\":10.3,\"lon\":9.7},\"product-name\":\"robot\",\"categories\":[\"one\",\"two\"]},\"id\":\"10203\",\"type\":\"products\"}}";

    List<String> categories = new ArrayList<>();
    categories.add("one");
    categories.add("two");

    HashMap<String, Boolean> availability = new HashMap<>();
    availability.put("here", true);
    availability.put("there", false);

    Location location = new Location();
    location.setLat(10.3);
    location.setLon(9.7);

    Product product = new Product();
    product.setId("10203");
    product.setName("robot");
    product.setCategories(categories);
    product.setPrice(10.3);
    product.setInStock(10);
    product.setAvailability(availability);
    product.setLocation(location);

    JsonApiObject jsonApiObject = new JsonApiObject();
    jsonApiObject.setResource(product);


    String json = morpheus.createJson(jsonApiObject, false);


    assertEquals(json, checkJson);
  }

  @Test
  public void testCreateJsonWithResourceRelationsNullify() {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    String checkJson = "{\"data\":{\"attributes\":{\"title\":\"Some title\"},\"id\":\"1\",\"type\":\"articles\",\"relationships\":{\"comments\":[],\"author\":null}}}";

    Article article = new Article();
    article.setId("1");
    article.setTitle("Some title");

    Author author = new Author();
    author.setId("2");
    author.setFirstName("Peter");
    article.setAuthor(author);

    Comment comment = new Comment();
    comment.setId("3");
    comment.setBody("body");

    ArrayList<Comment> comments = new ArrayList<>();
    comments.add(comment);
    comments.add(comment);
    article.setComments(comments);

    article.addRelationshipToNull("author");
    article.addRelationshipToNull("comments");

    JsonApiObject jsonApiObject = new JsonApiObject();
    jsonApiObject.setResource(article);


    String json = morpheus.createJson(jsonApiObject, false);


    assertEquals(json, checkJson);
  }

  // helper

  private String loadJSONFromAsset(int file) {
    String json = null;
    try {
      InputStream is = getInstrumentation().getContext().getResources().openRawResource(file);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");
    } catch (IOException ex) {
      fail("Was not able to load raw resource: " + file);
    }
    return json;
  }
}