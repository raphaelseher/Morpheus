package at.rags.morpheus;

import android.test.InstrumentationTestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.rags.morpheus.annotations.JsonApiType;
import at.rags.morpheus.exceptions.NotExtendingResourceException;
import at.rags.morpheus.testresources.Article;
import at.rags.morpheus.testresources.Author;
import at.rags.morpheus.testresources.BasicExpert;
import at.rags.morpheus.testresources.BasicPerson;
import at.rags.morpheus.testresources.ChatRoom;
import at.rags.morpheus.testresources.ChatSession;
import at.rags.morpheus.testresources.ChildArticle;
import at.rags.morpheus.testresources.ClinicalQueueItem;
import at.rags.morpheus.testresources.Comment;
import at.rags.morpheus.testresources.Gender;
import at.rags.morpheus.testresources.Location;
import at.rags.morpheus.testresources.Product;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
            morpheus.parse(loadJSONFromResource("articles.json"));

        assertTrue(jsonApiObject.getResources().size() == 1);
        assertTrue(jsonApiObject.getResources().get(0).getClass() == Article.class);
        Article article = (Article) jsonApiObject.getResources().get(0);
        assertTrue(article.getId().equals("1"));
        assertEquals("articles", article.getType());
        assertTrue(article.getTitle().equals("JSON API paints my bikeshed!"));
    }

    @Test
    public void testDataObject() throws Exception {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JsonApiObject jsonApiObject =
            morpheus.parse(loadJSONFromResource("article.json"));

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
            morpheus.parse(loadJSONFromResource("article.json"));

        assertNotNull(jsonApiObject.getResource());
        assertTrue(jsonApiObject.getResource().getClass() == Article.class);
        Article article = (Article) jsonApiObject.getResource();
        assertTrue(article.getId().equals("1"));
        assertTrue(article.getTitle().equals("JSON API paints my bikeshed!"));
        assertNotNull(article.getMeta());
        assertEquals(article.getMeta().get("test-meta"), "yes");
    }

    @Test
    public void testWithParentAttributes() throws NotExtendingResourceException, JSONException {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("child_article", ChildArticle.class);
        String jsonString = "{\"data\":{\"attributes\":{\"title\":null, \"child\":\"a child\", \"child_id\":null},\"id\":\"1\",\"type\":\"child_article\"}}";
        JsonApiObject jsonApiObject = morpheus.parse(jsonString);
        assertNotNull(jsonApiObject.getResource());
        ChildArticle childArticle = (ChildArticle) jsonApiObject.getResource();
        assertEquals(null, childArticle.getTitle());
        assertEquals("a child", childArticle.getChild());
        assertEquals(0, childArticle.getChildId());
    }

    @Test
    public void testComplicatedModel() throws NotExtendingResourceException, JSONException, IOException {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass(ClinicalQueueItem.class.getAnnotation(JsonApiType.class).value(), ClinicalQueueItem.class);
        Deserializer.registerResourceClass(ChatSession.class.getAnnotation(JsonApiType.class).value(), ChatSession.class);
        Deserializer.registerResourceClass(ChatRoom.class.getAnnotation(JsonApiType.class).value(), ChatRoom.class);
        Deserializer.registerResourceClass(BasicExpert.class.getAnnotation(JsonApiType.class).value(), BasicExpert.class);
        Deserializer.registerResourceClass(BasicPerson.class.getAnnotation(JsonApiType.class).value(), BasicPerson.class);

        JsonApiObject jsonApiObject = morpheus.parse(loadJSONFromResource("chatsession.json"));
        assertNotNull(jsonApiObject.getResources());
        assertTrue(jsonApiObject.getResources().get(0) instanceof ChatSession);
        ChatSession chatSession = (ChatSession) jsonApiObject.getResources().get(0);
        assertEquals("29064", chatSession.getId());
        assertEquals("1272", chatSession.getChatRoom().getId());
        assertEquals("7999209", chatSession.getChatRoom().getPin());
        assertEquals("initiated", chatSession.getState());
        assertEquals("Addiction medicine", chatSession.getExpert().getSpecialty());
        assertEquals("Dr. Expert", chatSession.getExpert().getName().getFullName());
        assertEquals(Gender.MALE, chatSession.getPatient().getGender());

        // Nested includes test
        jsonApiObject = morpheus.parse(loadJSONFromResource("clinicalqueue.json"));
        assertNotNull(jsonApiObject.getResources());
        assertTrue(jsonApiObject.getResources().get(0) instanceof ClinicalQueueItem);
        ClinicalQueueItem clinicalqueue = (ClinicalQueueItem) jsonApiObject.getResources().get(0);
        assertEquals("9552780", clinicalqueue.getChatSession().getChatRoom().getPin());
    }

    @Test
    public void testRelationship() throws Exception {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JsonApiObject jsonApiObject =
            morpheus.parse(loadJSONFromResource("articles.json"));

        assertTrue(jsonApiObject.getResources().size() == 1);
        assertTrue(jsonApiObject.getResources().get(0).getClass() == Article.class);
        Article article = (Article) jsonApiObject.getResources().get(0);
        assertNotNull(article.getAuthor());
        assertTrue(article.getComments().size() == 2);
    }

    @Test
    public void testRelationshipMetas() throws Exception {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JsonApiObject jsonApiObject =
            morpheus.parse(loadJSONFromResource("article.json"));

        assertTrue(jsonApiObject.getResource().getRelationshipMetas().get("author") != null);
        assertEquals("test", jsonApiObject.getResource().getRelationshipMetas().get("author").getString("test"));
    }

    @Test
    public void testIncluded() throws Exception {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JsonApiObject jsonApiObject =
            morpheus.parse(loadJSONFromResource("articles.json"));

        assertTrue(jsonApiObject.getIncluded().size() == 3);
    }

    @Test
    public void testIncludedRelations() throws Exception {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JsonApiObject jsonApiObject =
            morpheus.parse(loadJSONFromResource("article.json"));

        assertNotNull(jsonApiObject.getResource());
        Article article = (Article) jsonApiObject.getResource();
        assertTrue(article.getAuthor().getFirstName().equals("Dan"));

        Comment comment = (Comment) article.getComments().get(0);
        assertTrue(comment.getBody().equals("First!"));
    }

    @Test
    public void testLinks() throws Exception {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JsonApiObject jsonApiObject =
            morpheus.parse(loadJSONFromResource("article.json"));

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
            morpheus.parse(loadJSONFromResource("article.json"));

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
            morpheus.parse(loadJSONFromResource("articles.json"));

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
            morpheus.parse(loadJSONFromResource("article.json"));

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
            morpheus.parse(loadJSONFromResource("product.json"));

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
        assertEquals(product.getTimes()[0], "9:14");
        assertEquals(product.getTimes()[1], "12 15");

    }

    @Test
    public void testErrors() throws Exception {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("products", Product.class);

        JsonApiObject jsonApiObject =
            morpheus.parse(loadJSONFromResource("error.json"));

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
    public void testCreateJsonWithResourceRelationsIncluded() throws JSONException {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JSONObject checkJson = new JSONObject("{\"included\":[{\"attributes\":{\"body\":\"body\"},\"id\":\"3\",\"type\":\"comments\"},{\"attributes\":{\"body\":\"body\"},\"id\":\"3\",\"type\":\"comments\"},{\"attributes\":{\"first-name\":\"Peter\"},\"id\":\"2\",\"type\":\"people\"}],\"data\":{\"attributes\":{\"title\":\"Some title\"},\"id\":\"1\",\"type\":\"articles\",\"relationships\":{\"comments\":{\"data\":[{\"id\":\"3\",\"type\":\"comments\"},{\"id\":\"3\",\"type\":\"comments\"}]},\"author\":{\"data\":{\"id\":\"2\",\"type\":\"people\"}}}}}");

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


        JSONObject json = new JSONObject(morpheus.createJson(jsonApiObject, true));


        JSONAssert.assertEquals(json, checkJson, true);
    }

    @Test
    public void testCreateJsonWithResourcesRelations() throws JSONException {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("articles", Article.class);
        Deserializer.registerResourceClass("people", Author.class);
        Deserializer.registerResourceClass("comments", Comment.class);

        JSONObject checkJson = new JSONObject("{\"data\":[{\"attributes\":{\"title\":\"Some title\"},\"id\":\"1\",\"type\":\"articles\"},{\"attributes\":{\"title\":\"Some title\"},\"id\":\"1\",\"type\":\"articles\"}]}");

        Article article = new Article();
        article.setId("1");
        article.setTitle("Some title");

        ArrayList<Resource> articles = new ArrayList<>();
        articles.add(article);
        articles.add(article);

        JsonApiObject jsonApiObject = new JsonApiObject();
        jsonApiObject.setResources(articles);


        JSONObject json = new JSONObject(morpheus.createJson(jsonApiObject, false));


        JSONAssert.assertEquals(json, checkJson, true);
    }

    @Test
    public void testCreateJsonAttributes() throws JSONException {
        Morpheus morpheus = new Morpheus();
        Deserializer.registerResourceClass("products", Product.class);

        JSONObject checkJson = new JSONObject("{\"data\":{\"attributes\":{\"stores-availability\":{\"there\":false,\"here\":true},\"price\":10.3,\"in-stock\":10,\"location\":{\"lat\":10.3,\"lon\":9.7},\"product-name\":\"robot\",\"categories\":[\"one\",\"two\"]},\"id\":\"10203\",\"type\":\"products\"}}");

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


        JSONObject json = new JSONObject(morpheus.createJson(jsonApiObject, false));

        JSONAssert.assertEquals(json, checkJson, true);
    }

    // helper

    private String loadJSONFromAsset(int file) {
        String json = null;
        try {
            InputStream is = RuntimeEnvironment.application.getResources().openRawResource(file);
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

    private String loadJSONFromResource(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(MorpheusMappingTests.class.getClassLoader().getResourceAsStream(fileName)));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}