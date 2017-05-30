package at.rags.morpheus.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import at.rags.morpheus.Resource;
import at.rags.morpheus.annotations.JsonApiType;

@JsonApiType("Person")
public class BasicPerson extends Resource implements Serializable {

    @SerializedName("name")
    private Name name;
    @SerializedName("photo")
    private Photo photo;
    @SerializedName("gender")
    private Gender gender;
    @SerializedName("dob")
    private String dob;

    public Name getName() {
        return name;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Gender getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

}