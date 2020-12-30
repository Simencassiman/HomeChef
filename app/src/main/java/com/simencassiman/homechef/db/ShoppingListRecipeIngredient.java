package com.simencassiman.homechef.db;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Amount;
import com.simencassiman.homechef.model.Ingredient;

import org.jetbrains.annotations.NotNull;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "shopping_list_recipe_ingredient",
        primaryKeys = { "list_id", "recipe_id", "name" },
        foreignKeys = {@ForeignKey( entity = ShoppingListRecipe.class,
                                    parentColumns = {"list_id","recipe_id"},
                                    childColumns = {"list_id","recipe_id"},
                                    onDelete = CASCADE)}
        )
public class ShoppingListRecipeIngredient extends ShoppingListIngredient{

    public ShoppingListRecipeIngredient(int listId, @NotNull RecipeIngredient recipeIngredient, boolean collected) {
        setListId(listId);
        this.recipeIngredient = recipeIngredient;
        setCollected(collected);
    }

    @Override
    @NotNull
    public Ingredient getIngredient() {
        return recipeIngredient.getIngredient();
    }

    public RecipeIngredient getRecipeIngredient(){return recipeIngredient;}

    public void setRecipeIngredient(@NotNull RecipeIngredient recipeIngredient) {
        this.recipeIngredient = recipeIngredient;
    }

    public int getRecipeId(){
        return recipeIngredient.getRecipeId();
    }

    @Override
    public String getName(){
        return recipeIngredient.getName();
    }

    @Override
    public Amount getAmount(){
        return recipeIngredient.getAmount();
    }

    public void setAmount(Amount amount){
        recipeIngredient.getIngredient().setAmount(amount);
    }

    @Embedded
    @NotNull
    private RecipeIngredient recipeIngredient;

    @Override
    public boolean isFromRecipe(){
        return true;
    }

    public static Builder builder() { return new Builder();}

    public static class Builder {

        int listId, recipeId, listPos;
        Ingredient ingredient;
        boolean collected = false;

        public Builder setListId(int id) {
            this.listId = id;
            return this;
        }

        public Builder setRecipeId(int id) {
            this.recipeId = id;
            return this;
        }

        public Builder setIngredient(Ingredient ingredient) throws IllegalArgumentException{
            if (ingredient == null && !Ingredient.isValidIngredient(ingredient.getName(),ingredient.getAmount()))
                throw new IllegalArgumentException("Invalid ingredient");

            this.ingredient = ingredient;
            return this;
        }

        public Builder setCollected(){
            this.collected = true;
            return this;
        }

        public ShoppingListRecipeIngredient build() {
            return new ShoppingListRecipeIngredient(listId, new RecipeIngredient(recipeId,ingredient), collected);
        }
    }
}
