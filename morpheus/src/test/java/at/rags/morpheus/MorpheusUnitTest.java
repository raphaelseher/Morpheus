package at.rags.morpheus;

import org.junit.Before;
import org.junit.Test;

import at.rags.morpheus.TestResources.Article;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MorpheusUnitTest {

  public Morpheus mMorpheus;

  @Before
  public void setup() {
    mMorpheus = new Morpheus();
    mMorpheus.registerResourceClass("articles", Article.class);
  }

  @Test
  public void testInitAndRegisterClass() throws Exception {
    Morpheus morpheus = new Morpheus();
    assertNotNull(morpheus);
  }

  @Test
  public void testDeserializeData() throws Exception {
    JSONAPIObject jsonapiObject =
        mMorpheus.jsonToObject(JSONString.jsonWithDataArrayLinksIncluded());

    assertTrue(jsonapiObject.getResources().size() == 1);
  }
}