package com.simencassiman.homechef.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.ShoppingList;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "shopping_list_recipe",
        primaryKeys = { "recipe_id", "list_id" },
        foreignKeys = { @ForeignKey(entity = Recipe.class,
                                    parentColumns = "id",
                                    childColumns = "recipe_id",
                                    onDelete = CASCADE),
                        @ForeignKey(entity = ShoppingList.class,
                                    parentColumns = "id",
                                    childColumns = "list_id",
                                    onDelete = CASCADE)}
        )
public class ShoppingListRecipe {

    public ShoppingListRecipe(int listId, int recipeId, boolean collected, int nrPeople) {
        this.listId = listId;
        this.recipeId = recipeId;
        this.collected = collected;
        this.nrPeople = nrPeople;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    @ColumnInfo(name = "list_id")
    private int listId;

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    @ColumnInfo(name = "recipe_id", index = true)
    private int recipeId;


    public int getNrPeople() {
        return nrPeople;
    }

    public void setNrPeople(int nrPeople) {
        this.nrPeople = nrPeople;
    }

    @ColumnInfo(name = "nr_people")
    private int nrPeople;

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    private boolean collected;
}
