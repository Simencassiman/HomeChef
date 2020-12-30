package com.simencassiman.homechef.viewmodels;

import androidx.lifecycle.ViewModel;

import com.simencassiman.homechef.db.Repository;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.Unit;

import javax.inject.Inject;

public class AddIngredientViewModel extends ViewModel {

    private Repository repository;

    @Inject
    public AddIngredientViewModel(Repository repo){
        this.repository = repo;
        this.name = "";
        this.selectedUnit = Unit.values()[0];
        this.listId = -1;
    }

    public int getListId(){
        return listId;
    }

    public void setListId(int id) throws IllegalArgumentException{
        if(id < 0)
            throw new IllegalArgumentException("Invalid id provided");
        this.listId = id;
    }

    private int listId;

    public String getName(){
        return name;
    }

    public void setName(String name) throws IllegalArgumentException{
        if(name == null)
            throw new IllegalArgumentException("Provided name cannot be null");
        this.name = name;
    }

    private String name;

    public String getAmount(){
        return amount;
    }

    public void setAmount(String amount){
        this.amount = amount;
    }

    private String amount;

    public Unit getUnit(){
        return selectedUnit;
    }

    public void setSelectedUnit(Unit unit) throws IllegalArgumentException{
        if(unit == null)
            throw new IllegalArgumentException("Provided unit cannot be null");
        this.selectedUnit = unit;
    }

    private Unit selectedUnit;

    public void saveIngredient() throws IllegalStateException{
        if(!Ingredient.isValidIngredient(getName(), getAmount(), getUnit()) || getListId() < 0)
            throw new IllegalStateException("Cannot form valid ingredient with current inputs");
        repository.insertShoppingListIngredient(listId,-1,
                Ingredient.builder().setName(name).setValue(amount).setUnit(selectedUnit).build());
    }
}
