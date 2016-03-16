package at.rags.morpheus;

import android.test.InstrumentationTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;

import at.rags.morpheus.Resources.Article;
import at.rags.morpheus.Resources.Author;
import at.rags.morpheus.Resources.Comment;
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

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertTrue(jsonapiObject.getResources().size() == 1);
    assertTrue(jsonapiObject.getResources().get(0).getClass() == Article.class);
    Article article = (Article)jsonapiObject.getResources().get(0);
    assertTrue(article.getId().equals("1"));
    assertTrue(article.getTitle().equals("JSON API paints my bikeshed!"));
  }

  @Test
  public void testDataObject() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonapiObject.getResource());
    assertTrue(jsonapiObject.getResource().getClass() == Article.class);
    Article article = (Article)jsonapiObject.getResource();
    assertTrue(article.getId().equals("1"));
    assertTrue(article.getTitle().equals("JSON API paints my bikeshed!"));
  }

  @Test
  public void testDataObjectMeta() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonapiObject.getResource());
    assertTrue(jsonapiObject.getResource().getClass() == Article.class);
    Article article = (Article)jsonapiObject.getResource();
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

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertTrue(jsonapiObject.getResources().size() == 1);
    assertTrue(jsonapiObject.getResources().get(0).getClass() == Article.class);
    Article article = (Article)jsonapiObject.getResources().get(0);
    assertNotNull(article.getAuthor());
    assertTrue(article.getComments().size() == 2);
  }

  @Test
  public void testIncluded() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertTrue(jsonapiObject.getIncluded().size() == 3);
  }

  @Test
  public void testIncludedRelations() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonapiObject.getResource());
    Article article = (Article)jsonapiObject.getResource();
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

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonapiObject.getResource());
    Article article = (Article)jsonapiObject.getResource();
    assertNotNull(article.getLinks());
    assertTrue(article.getLinks().selfLink.equals("http://example.com/articles/1"));
    assertNull(article.getLinks().related);
  }

  @Test
  public void testPaginationLinks() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonapiObject.getLinks());
    assertTrue(jsonapiObject.getLinks().selfLink.equals("http://example.com/articles"));
    assertTrue(jsonapiObject.getLinks().next.equals("http://example.com/articles?page[offset]=2"));
    assertTrue(jsonapiObject.getLinks().last.equals("http://example.com/articles?page[offset]=10"));
    assertNull(jsonapiObject.getLinks().related);
  }

  @Test
  public void testMeta() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

    assertNotNull(jsonapiObject.getMeta());
    assertTrue(jsonapiObject.getMeta().get("testmeta").equals("yes"));
  }

  @Test
  public void testAttributesArray() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    Deserializer.registerResourceClass("people", Author.class);
    Deserializer.registerResourceClass("comments", Comment.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.article));

    assertNotNull(jsonapiObject.getLinks());
    Article article = (Article)jsonapiObject.getResource();

    assertTrue(article.getTags().get(0).equals("main"));
    assertTrue(article.getTags().get(1).equals("dev"));
  }

  @Test
  public void testAttributesTypes() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("products", Product.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.product));

    Product product = (Product)jsonapiObject.getResources().get(0);

    assertTrue(product.getId().equals("123456"));
    assertTrue(product.getName().equals("Fancy new roboter"));
    assertTrue(product.getPrice() == 999.75);
    assertTrue(product.getInStock() == 9);
    assertTrue(product.getAvailability().get("Store 1"));
    assertFalse(product.getAvailability().get("Store 3"));
  }

  @Test
  public void testErrors() throws Exception {
    Morpheus morpheus = new Morpheus();
    Deserializer.registerResourceClass("products", Product.class);

    JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.error));

    assertNotNull(jsonapiObject.getErrors());
    assertEquals(jsonapiObject.getErrors().get(0).getStatus(), "403");
    assertEquals(jsonapiObject.getErrors().get(0).getSource().getPointer(), "/data/attributes/secret-powers");
    assertEquals(jsonapiObject.getErrors().get(0).getDetail(), "Editing secret powers is not authorized on Sundays.");

    assertEquals(jsonapiObject.getErrors().get(1).getStatus(), "422");
    assertEquals(jsonapiObject.getErrors().get(1).getId(), "1");
    assertEquals(jsonapiObject.getErrors().get(1).getCode(), "2");
    assertEquals(jsonapiObject.getErrors().get(1).getSource().getPointer(), "/data/attributes/volume");
    assertEquals(jsonapiObject.getErrors().get(1).getSource().getParameter(), "/data/attributes/battery");
    assertEquals(jsonapiObject.getErrors().get(1).getTitle(), "some title");
    assertEquals(jsonapiObject.getErrors().get(1).getDetail(), "Volume does not, in fact, go to 11.");
    assertEquals(jsonapiObject.getErrors().get(1).getLinks().getAbout(), "about.com");

    assertEquals(jsonapiObject.getErrors().get(2).getStatus(), "500");
    assertEquals(jsonapiObject.getErrors().get(2).getSource().getPointer(), "/data/attributes/reputation");
    assertNull(jsonapiObject.getErrors().get(2).getSource().getParameter());
    assertEquals(jsonapiObject.getErrors().get(2).getTitle(), "The backend responded with an error");
    assertEquals(jsonapiObject.getErrors().get(2).getDetail(), "Reputation service not responding after three requests.");
  }

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
      ex.printStackTrace();
    }
    return json;
  }
}