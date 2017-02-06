# Morpheus

 [![Download](https://api.bintray.com/packages/xamoom/maven/Morpheus/images/download.svg) ](https://bintray.com/xamoom/maven/Morpheus/_latestVersion)
 [![Build Status](https://travis-ci.org/xamoom/Morpheus.svg?branch=master)](https://travis-ci.org/xamoom/Morpheus)

Morpheus is a [JSONAPI](http://jsonapi.org/) deserializer for android that uses java reflection.
You can define your own java classes to deserialize.

Take a look at the [documentation](http://xamoom.github.io/Morpheus/docs/0.5.1/index.html).

## Install

 ```java
 compile 'com.xamoom.android:morpheus:0.5.1'
 ```

## Usage

Prepare your resources

1. extend Resource
2. Use @SerializedName() annotation when your field name differs from the json.
3. Create relationship mapping with the @Relationship() annotation.

```java
public class Article extends Resource {
  @SerializedName("article-title")
  private String title;
  @Relationship("author")
  private Author author;
  @Relationship("comments")
  private List<Comment> comments;
}
```
### Deserialize

1. Create a Morpheus instance
2. Register your resources
3. parse your JSON string

```java
Morpheus morpheus = new Morpheus();
//register your resources
Deserializer.registerResourceClass("articles", Article.class);
Deserializer.registerResourceClass("people", Author.class);
Deserializer.registerResourceClass("comments", Comment.class);
JsonApiObject jsonApiObject =
        morpheus.parse(loadJSONFromAsset(R.raw.articles));

Article article = (Article)jsonApiObject.getResources().get(0);
Log.v(TAG, "Article Id: " + article.getId())
```

### Serialize

```java
Morpheus morpheus = new Morpheus();
Deserializer.registerResourceClass("products", Product.class);

JsonApiObject jsonApiObject = new JsonApiObject();
jsonApiObject.setResource(product);

String json = morpheus.createJson(jsonApiObject, false);
```

Delete an relationship:
```java
article.addRelationshipToNull("author");

JsonApiObject jsonApiObject = new JsonApiObject();
jsonApiObject.setResource(article);

String articleJson = morpheus.createJson(jsonApiObject, false);

```

# Development status
Morpheus can:

* deserialize data object or array
* deserialize relationships
* map includes to relationships
* deserialize links, meta, errors
* serialize resources with their relationships and includes

# Data Attribute Mapping
At the moment Morpheus maps

* Strings -> `String`
* Floats -> `double`
* Booleans -> `boolean`
* JSONArrays -> `List<Object>` (with Gson)
* JSONObject -> `HashMap<String, Object>` (with Gson)

You can write your own AttributeMapper by extending `AttributeMapper.java` and initialize Morpheus with your mapper.

# Contribution
If you want to contribute: make your changes and do a pull request.
