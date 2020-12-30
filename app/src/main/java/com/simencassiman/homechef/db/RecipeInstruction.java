package com.simencassiman.homechef.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.simencassiman.homechef.model.Recipe;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "recipe_step",
        primaryKeys = {"recipe_id","step_nr"},
        foreignKeys = @ForeignKey(entity = Recipe.class,
                                parentColumns = "id",
                                childColumns = "recipe_id",
                                onDelete = CASCADE))
public class RecipeInstruction {

    public RecipeInstruction(int recipeId, int instructionNr, String text) {
        this.recipeId = recipeId;
        this.instructionNr = instructionNr;
        this.text = text;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
    @ColumnInfo(name="recipe_id")
    private int recipeId;

    public int getInstructionNr() {
        return instructionNr;
    }

    public void setInstructionNr(int instructionNr) {
        this.instructionNr = instructionNr;
    }
    @ColumnInfo(name = "step_nr")
    private int instructionNr;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;
}
