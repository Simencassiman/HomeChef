package com.simencassiman.homechef.model;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.simencassiman.homechef.db.RecipeCollector;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity(tableName = "shopping_list")
public class ShoppingList {

    public ShoppingList(int id, String name){
        this(id, name, null, null,null);
    }

    public ShoppingList(int id, String name, Date date, LiveData<List<RecipeCollector>> recipes,  LiveData<Map<Integer,Boolean>> collections){
        this(id, name, date, null, recipes, collections);
    }

    public ShoppingList(int id, String name, Date date, LiveData<List<Ingredient>>ingredients,
                        LiveData<List<RecipeCollector>> recipes, LiveData<Map<Integer,Boolean>> collections){
        if(recipes!= null && collections != null && recipes.getValue() != null && collections.getValue() != null &&
                recipes.getValue().size() != collections.getValue().size())
            throw new IllegalArgumentException("Length of recipes doesn't match the collections length.");

        this.id = id;
        this.name = name;
        this.shoppingDate = date;
        this.recipes = recipes;
        this.ingredients = ingredients;
        this.reminder = date != null;

    }

    public int getId(){ return this.id;}

    public void setId(int id){ this.id = id;}

    @PrimaryKey
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Date getShoppingDate() {
        return shoppingDate;
    }

    public void setShoppingDate(Date shoppingDate) {
        this.shoppingDate = shoppingDate;
    }

    @ColumnInfo(name = "date")
    private Date shoppingDate;


    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    private boolean reminder;

    public LiveData<List<Ingredient>> getIngredients(){
        return ingredients;
    }

    public void setIngredients(LiveData<List<Ingredient>> ingredients){
        this.ingredients = ingredients;
    }

    @Ignore
    private LiveData<List<Ingredient>> ingredients;

    public LiveData<List<RecipeCollector>> getRecipes(){
        return recipes;
    }

    public void setRecipies(LiveData<List<RecipeCollector>> recipes){
        this.recipes = recipes;
    }

    @Ignore
    private LiveData<List<RecipeCollector>> recipes;

    public LiveData<Map<Integer,Boolean>> getCollections(){
        return collections;
    }

    public void setCollections(LiveData<Map<Integer, Boolean>> collections)
            throws IllegalArgumentException, IllegalStateException{
//        if((recipes == null || recipes.getValue() == null) && collections != null
//                && collections.getValue() != null)
//            throw new IllegalStateException("Variable recipes must be set first.");
//        if(collections == null || collections.getValue() == null)
//            throw new IllegalArgumentException("Cannot set collections to null if recipes isn't null");
//        if(recipes.getValue().size() != collections.getValue().size())
//            throw new IllegalArgumentException("Collections and recipes must be the same length");

        this.collections = collections;
    }

    @Ignore
    private LiveData<Map<Integer,Boolean>> collections;

}
