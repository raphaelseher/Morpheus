package at.rags.morpheus;

import org.junit.Test;

import java.util.HashMap;

import at.rags.morpheus.exceptions.NotExtendingResourceException;
import at.rags.morpheus.resources.Article;
import at.rags.morpheus.resources.FalseResource;
import at.rags.morpheus.resources.InterfaceArticle;
import at.rags.morpheus.resources.MultiExtendResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by raphaelseher on 11/03/16.
 */
public class DeserializerTest {

    @Test
    public void testCreateObjectFromString() throws Exception {
        HashMap<String, Class> mockMap = mock(HashMap.class);
        when(mockMap.get("articles")).thenReturn(Article.class);
        Deserializer.setRegisteredClasses(mockMap);
        Deserializer deserializer = new Deserializer();

        Resource resource = deserializer.createObjectFromString("articles");

        assertNotNull(resource);
        assertEquals(resource.getClass(), Article.class);
    }

    @Test(expected = InstantiationException.class)
    public void testCreateObjectFromStringInstantiationException() throws Exception {
        HashMap<String, Class> mockMap = mock(HashMap.class);
        when(mockMap.get("articles")).thenReturn(InterfaceArticle.class);
        Deserializer.setRegisteredClasses(mockMap);
        Deserializer deserializer = new Deserializer();

        Resource resource = deserializer.createObjectFromString("articles");
    }

    @Test(expected = NotExtendingResourceException.class)
    public void testCreateObjectFromStringClassCastException() throws Exception {
        HashMap<String, Class> mockMap = mock(HashMap.class);
        when(mockMap.get("test")).thenReturn(FalseResource.class);
        Deserializer.setRegisteredClasses(mockMap);
        Deserializer deserializer = new Deserializer();

        Resource resource = deserializer.createObjectFromString("test");
    }

    @Test
    public void testSetField() throws Exception {
        Deserializer deserializer = new Deserializer();
        Article article = new Article();

        Resource resource = deserializer.setField(article, "title", "My Title");

        article = (Article) resource;
        assertEquals(article.getTitle(), "My Title");
    }

    @Test
    public void testSetFieldNoSuchFieldException() throws Exception {
        Deserializer deserializer = new Deserializer();
        Article article = new Article();

        Resource resource = deserializer.setField(article, "asdf", "My Title");

        article = (Article) resource;
        assertNotNull(article);
    }

    @Test
    public void testSetIdField() throws Exception {
        Deserializer deserializer = new Deserializer();
        Article article = new Article();

        Resource resource = deserializer.setIdField(article, "123456");

        article = (Article) resource;
        assertEquals(article.getId(), "123456");
    }

    @Test
    public void testSetIdFieldNumber() throws Exception {
        Deserializer deserializer = new Deserializer();
        Article article = new Article();

        Resource resource = deserializer.setIdField(article, 123456);

        article = (Article) resource;
        assertEquals(article.getId(), "123456");
    }

    @Test
    public void testSetIdFieldMultiExtendingClass() throws Exception {
        Deserializer deserializer = new Deserializer();
        MultiExtendResource multiExtendResource = new MultiExtendResource();

        Resource resource = deserializer.setIdField(multiExtendResource, 123456);

        multiExtendResource = (MultiExtendResource) resource;
        assertEquals(multiExtendResource.getId(), "123456");
    }

}
