package at.rags.morpheus.Resources;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wuhaoouyang on 3/15/17.
 */

public class ChildArticle extends Article {

    @SerializedName("child")
    private String child;

    public String getChild() {
        return child;
    }
}
