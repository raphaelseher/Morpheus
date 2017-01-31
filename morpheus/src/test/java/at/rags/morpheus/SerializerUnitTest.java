package at.rags.morpheus;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.ArrayMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import at.rags.morpheus.TestResources.Article;
import at.rags.morpheus.TestResources.Author;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by raphaelseher on 16/09/16.
 */
@RunWith(JUnit4.class)
public class SerializerUnitTest {
  private Serializer serializer;

  @Before
  public void setup() {
    serializer = new Serializer();
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  @Test
  public void testGetFieldsAsDictionary() {
    Article article = new Article();
    article.setTitle("title");
    article.setPublicStatus(true);
    ArrayList<String> tags = new ArrayList<String>();
    tags.add("tag1");
    article.setTags(tags);
    ArrayMap<String, String> testmap = new ArrayMap<>();
    testmap.put("key", "value");
    article.setMap(testmap);
    article.setVersion(1);
    article.setPrice(1.0);

    Map<String, Object> checkMap = new HashMap<>();
    checkMap.put("price", 1.0);
    checkMap.put("public", "true");
    checkMap.put("title", "title");
    checkMap.put("map", testmap);
    checkMap.put("version", 1);
    checkMap.put("tags", tags);

    Map<String, Object> map = serializer.getFieldsAsDictionary(article);

    assertNotNull(map);
    assertEquals(checkMap.toString(), map.toString());
  }

  @Test
  public void testGetFieldsAsDictionaryWithoutAttributes() {
    Author author = new Author();
    author.setId("id");

    Map<String, Object> checkMap = new HashMap<>();
    checkMap.put("id", "id");

    Map<String, Object> map = serializer.getFieldsAsDictionary(author);

    assertNull(map);
  }

  @Test
  public void testGetRelationships() {
    Article article = new Article();
    Author author = new Author();
    article.setAuthor(author);

    ArrayList<Author> authors = new ArrayList<>();
    authors.add(author);
    authors.add(author);
    article.setAuthors(authors);

    HashMap<String, Object> checkMap = new HashMap<>();
    checkMap.put("author", author);
    checkMap.put("authors", authors);


    HashMap<String, Object> output = serializer.getRelationships(article);


    assertEquals(output.toString(), checkMap.toString());
  }

}
