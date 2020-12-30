package com.simencassiman.homechef.db;

import androidx.room.ColumnInfo;

import com.simencassiman.homechef.model.Amount;
import com.simencassiman.homechef.model.Ingredient;

public abstract class ShoppingListIngredient {

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    @ColumnInfo(name = "list_id")
    private int listId;

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    private boolean collected;

    abstract Ingredient getIngredient();
    abstract String getName();
    abstract Amount getAmount();
    abstract boolean isFromRecipe();
//    abstract int getListPosition();
}
