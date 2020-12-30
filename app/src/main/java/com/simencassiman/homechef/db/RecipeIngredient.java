package com.simencassiman.homechef.db;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Amount;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.Recipe;

import org.jetbrains.annotations.NotNull;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "recipe_ingredient",
        primaryKeys = { "recipe_id", "name" },
        foreignKeys = @ForeignKey(  entity = Recipe.class,
                                    parentColumns = "id",
                                    childColumns = "recipe_id",
                                    onDelete = CASCADE))
public class RecipeIngredient {

    public RecipeIngredient(int recipeId, @NotNull Ingredient ingredient){
        this.recipeId = recipeId;
        this.ingredient = ingredient;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    @ColumnInfo(name = "recipe_id")
    private int recipeId;

    @NotNull
    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(@NotNull Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Embedded
    @NotNull
    private Ingredient ingredient;

    public String getName(){
        return ingredient.getName();
    }

    public Amount getAmount(){
        return ingredient.getAmount();
    }

}
