package at.rags.morpheus.testresources;

import com.google.gson.annotations.SerializedName;

import at.rags.morpheus.Resource;
import at.rags.morpheus.annotations.JsonApiType;

/**
 * Created by raphaelseher on 05/03/16.
 */
@JsonApiType("Author")
public class Author extends Resource {

    @SerializedName("first-name")
    private String firstName;

    @SerializedName("last-name")
    private String lastName;

    @SerializedName("twitter")
    private String twitterHandle;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }
}
