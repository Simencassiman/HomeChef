package com.simencassiman.homechef.db;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Amount;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.ShoppingList;

import org.jetbrains.annotations.NotNull;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "shopping_list_pure_ingredient",
        primaryKeys = { "list_id", "name", "unit"},
        foreignKeys = {@ForeignKey( entity = ShoppingList.class,
                parentColumns = "id",
                childColumns = "list_id",
                onDelete = CASCADE)})
public class ShoppingListPureIngredient extends ShoppingListIngredient {

    public ShoppingListPureIngredient(int listId, @NotNull Ingredient ingredient, boolean collected) {
        setListId(listId);
        this.ingredient = ingredient;
        setCollected(collected);
    }

    @Override
    public Ingredient getIngredient(){
        return ingredient;
    }

    @Override
    public String getName() {
        return ingredient.getName();
    }

    @Override
    Amount getAmount() {
        return ingredient.getAmount();
    }

    @Embedded
    @NotNull
    private Ingredient ingredient;

    @Override
    public boolean isFromRecipe() {
        return false;
    }
}
