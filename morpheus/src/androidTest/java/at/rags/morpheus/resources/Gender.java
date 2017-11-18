package at.rags.morpheus.resources;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wuhaoouyang on 5/30/17.
 */

public enum Gender {
    @SerializedName("male")
    MALE,
    @SerializedName("female")
    FEMALE,
    UNKNOWN
}
