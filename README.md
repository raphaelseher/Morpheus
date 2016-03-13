# Morpheus

Morpheus is a [JSONAPI](http://jsonapi.org/) deserializer for android that uses java reflection.
You can define your own java classes to deserialize.

## Usage

Prepare your resources
```java
public class Article extends Resource {
  @SerializeName("article-title")
  private String title;
  @Relationship("author")
  private Author author;
  @Relationship("comments")
  private List<Comment> comments;
}
```
Deserialize your data
```java
Morpheus morpheus = new Morpheus();
//register your resources
Deserializer.registerResourceClass("articles", Article.class);
Deserializer.registerResourceClass("people", Author.class);
Deserializer.registerResourceClass("comments", Comment.class);
JSONAPIObject jsonapiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));
        
Article article = (Article)jsonapiObject.getResources().get(0);
Log.v(TAG, "Article Id: " + article.getId())
```

# Development status
Morpheus can (0.3.0):
* deserialize data object or array
* deserialize relationships
* map includes to relationships
* deserialize links, meta, errors

# Contribution
If you want to contritbute make your changes and make and pull request. I am thankfull for every help.
If you find bugs or have problems, please make an issue.
I am also very happy for every message that help me to increase my skills.

