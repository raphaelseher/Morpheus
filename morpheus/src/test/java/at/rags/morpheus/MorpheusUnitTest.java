package at.rags.morpheus;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MorpheusUnitTest {

  @Test
  public void testInitAndRegisterClass() throws Exception {
    Morpheus morpheus = new Morpheus();
    assertNotNull(morpheus);
  }

}