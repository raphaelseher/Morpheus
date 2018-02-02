package at.rags.morpheus.resources;

import at.rags.morpheus.Resource;

/**
 * Created by raphaelseher on 10/03/16.
 */
public class Author extends Resource {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
