package com.simencassiman.homechef.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Recipe;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "favorite_recipe",
        primaryKeys = {"recipe_id"},
        foreignKeys = @ForeignKey(entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipe_id",
                onDelete = CASCADE))
public class FavoriteRecipe {

    FavoriteRecipe(int recipeId, int userId){
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    @ColumnInfo(name = "recipe_id")
    private final int recipeId;

    public int getUserId() {
        return userId;
    }

    @ColumnInfo(name = "user_id")
    private final int userId;
}
