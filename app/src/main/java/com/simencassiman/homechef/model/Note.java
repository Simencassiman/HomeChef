package com.simencassiman.homechef.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note")
public class Note {

    public Note(int id, int recipeId, int peopleCount, int time, String utilities) {
        this.id = id;
        this.recipeId = recipeId;
        this.peopleCount = peopleCount;
        this.time = time;
        this.utilities = utilities;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey
    private int id;

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    @ColumnInfo(name="recipe_id")
    private int recipeId;

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    @ColumnInfo(name="people_count")
    private int peopleCount;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private int time;

    public String getUtilities() {
        return utilities;
    }

    public void setUtilities(String utilities) {
        this.utilities = utilities;
    }

    private String utilities;
}
