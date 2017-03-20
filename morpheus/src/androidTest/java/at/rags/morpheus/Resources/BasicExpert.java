package at.rags.morpheus.Resources;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import at.rags.morpheus.annotations.JsonApiType;

@JsonApiType("Expert")
public class BasicExpert extends BasicPerson implements Serializable {
    @SerializedName("name")
    private Name name;
    @SerializedName("photo")
    private Photo photo;
    @SerializedName("specialty")
    private String specialty;

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Photo getPhoto() {
        return photo;
    }

    public String getSpecialty() {
        return specialty;
    }

}