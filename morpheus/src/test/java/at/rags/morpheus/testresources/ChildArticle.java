package at.rags.morpheus.testresources;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wuhaoouyang on 3/15/17.
 */

public class ChildArticle extends Article {

    @SerializedName("child")
    private String child;
    @SerializedName("child_id")
    private int childId;

    public String getChild() {
        return child;
    }

    public int getChildId() {
        return childId;
    }
}
