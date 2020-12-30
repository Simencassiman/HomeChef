package com.simencassiman.homechef.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Recipe;

import org.jetbrains.annotations.NotNull;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "recipe_tag",
        primaryKeys = {"recipe_id", "tag"},
        foreignKeys = @ForeignKey(entity = Recipe.class,
                                 parentColumns = "id",
                                 childColumns = "recipe_id",
                                 onDelete = CASCADE))
public class RecipeTag {

    public RecipeTag(int recipeId, @NotNull String tag) {
        this.recipeId = recipeId;
        this.tag = tag;
    }

    public int getRecipeId() {
        return recipeId;
    }

    @ColumnInfo(name = "recipe_id")
    private final int recipeId;

    public String getTag() {
        return tag;
    }

    @NotNull
    private final String tag;
}
