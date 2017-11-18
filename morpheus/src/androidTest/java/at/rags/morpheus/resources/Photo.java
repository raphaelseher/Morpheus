package at.rags.morpheus.resources;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable {

    @SerializedName("large")
    private String large;
    @SerializedName("thumb")
    private String thumb;

    public String getLarge() {
        return large;
    }

    public String getThumb() {
        return thumb;
    }
}
