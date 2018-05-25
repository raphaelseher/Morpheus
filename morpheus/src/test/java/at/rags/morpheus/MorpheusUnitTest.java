package at.rags.morpheus;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import at.rags.morpheus.resources.Author;
import at.rags.morpheus.testresources.BasicExpert;
import at.rags.morpheus.testresources.BasicPerson;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MorpheusUnitTest {

    private Morpheus morpheus;

    @Before
    public void setup() {
        morpheus = new Morpheus();

        HashMap<String, Class> mockMap = mock(HashMap.class);
        when(mockMap.get("authors")).thenReturn(Author.class);
        HashSet<String> set = new HashSet<>();
        set.add("authors");
        when(mockMap.keySet()).thenReturn(set);
        Deserializer.setRegisteredClasses(mockMap);
    }

    @Test
    public void testInit() throws Exception {
        Morpheus morpheus = new Morpheus();
        assertNotNull(morpheus);
    }

    @Test
    public void testResource() {
        BasicPerson person = new BasicPerson();
        person.setId("1");
        BasicExpert expert = new BasicExpert();
        expert.setId("1");
        assertTrue(person.equals(expert));
        assertTrue(expert.equals(person));
    }
}