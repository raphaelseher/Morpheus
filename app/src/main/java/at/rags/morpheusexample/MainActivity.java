package at.rags.morpheusexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import at.rags.morpheus.JSONAPIObject;
import at.rags.morpheus.Morpheus;
import at.rags.morpheus.Deserializer;
import at.rags.morpheusexample.JsonApiResources.Article;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Morpheus morph = new Morpheus();
    Deserializer.registerResourceClass("articles", Article.class);
    JSONAPIObject jsonapiObject = null;
    try {
      jsonapiObject = morph.jsonToObject(loadJSONFromAsset());
    } catch (Exception e) {
      e.printStackTrace();
    }
    Article article = (Article) jsonapiObject.getResources().get(0);
    Log.v(TAG, "ID?: " + article.getId());
    Log.v(TAG, "Title?: " + article.getTitle());
  }

  private String loadJSONFromAsset() {
    String json = null;
    try {
      InputStream is = getResources().openRawResource(R.raw.articles);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
    return json;
  }
}
