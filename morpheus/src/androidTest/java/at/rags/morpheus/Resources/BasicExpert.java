package at.rags.morpheus.Resources;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import at.rags.morpheus.annotations.JsonApiType;

@JsonApiType("Expert")
public class BasicExpert extends BasicPerson implements Serializable {
    @SerializedName("specialty")
    private String specialty;

    public String getSpecialty() {
        return specialty;
    }

}