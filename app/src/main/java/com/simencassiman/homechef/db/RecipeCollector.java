package com.simencassiman.homechef.db;

import com.simencassiman.homechef.model.Recipe;

public class RecipeCollector extends Recipe{

    public RecipeCollector(int id, String title, String imageURI, String author, String description, EditState state, int peopleCount) {
        super(id, title, imageURI, author, description, state, peopleCount);
    }

    public boolean collected;
}
