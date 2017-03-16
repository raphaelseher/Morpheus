package at.rags.morpheus.TestResources;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wuhaoouyang on 3/15/17.
 */

public class ChildArticle extends Article {

    @SerializedName("child_property")
    private String childProperty;

    public String getChildProperty() {
        return childProperty;
    }
}
