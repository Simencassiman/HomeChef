package com.simencassiman.homechef.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_test")
public class RecipeTest {

    @PrimaryKey
    private int id;

    private String title;

    @ColumnInfo(name = "people_counter")
    private int peopleCounter;

    public RecipeTest(int id, String title, int peopleCounter) {
        this.id = id;
        this.title = title;
        this.peopleCounter = peopleCounter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        title = title;
    }

    public int getPeopleCounter() {
        return peopleCounter;
    }

    public void setPeopleCounter(int peopleCounter) {
        this.peopleCounter = peopleCounter;
    }
}
